; dix.el -- minor mode for editing Apertium dictionary files

; See http://wiki.apertium.org 

;; This program is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.
;; 
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.
;; 
;; You should have received a copy of the GNU General Public License
;; along with this program; if not, write to the Free Software
;; Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

;;; Usage:
;; (add-to-list 'load-path "/path/to/dix.el")
;; (require 'dix)
;; (add-hook 'nxml-mode-hook
;; 	  (lambda () (and buffer-file-name
;; 			  (string-match "\\.dix$" buffer-file-name)
;; 			  (dix-mode 1))))
;;; 
;;; `C-c L' now creates an LR-restricted copy of the <e>-element at
;;; point, `C-c R' an RL-restricted one. `C-TAB' cycles through the
;;; restriction possibilities (LR, RL, none), while `M-n' and `M-p'
;;; move to the next and previous "important bits" of <e>-elements
;;; (just try it!). `C-c S' sorts a pardef, while `C-c G' moves point
;;; to the pardef of the entry at point, leaving mark where you left
;;; from. Inside a pardef, `C-c A' shows all usages of that pardef
;;; within the dictionaries represented by the string `dix-dixfiles',
;;; while `C-c D' gives you a list of all pardefs which use these
;;; suffixes (where a suffix is the contents of an <l>-element).
;;; 
;;; `M-x dix-suffix-sort' is a general function, useful outside of dix
;;; XML files too, that just reverses each line, sorts them, and
;;; reverses them back. `C-c % %' is a convenience function for
;;; regexp-replacing text within certain XML elements, eg. all <e>
;;; elements; `C-c % r' and `C-c % l' are specifically for <r> and <l>
;;; elements, respectively.
;;; 
;;; 
;;; I like having the following set too:
;; (setq nxml-sexp-element-flag t 		; treat <e>...</e> as a sexp
;;       rng-nxml-auto-validate-flag nil)       ; 8MB of XML takes a while
;;; You can always turn on validation again with C-c C-v.
;;; 
;;; 
;;; I haven't bothered with defining a real indentation function, but
;;; if you like having all <i> elements aligned at eg. column 25, the
;;; align rules defined here let you do M-x align on a region to
;;; achieve that, and also aligns <p> and <r>. Set your favorite
;;; column numbers with M-x customize-group RET dix.
;;; 
;;; 
;;; Plan / long term todo: 
;;; - Functions shouldn't modify the kill-ring.
;;; - Functions should be agnostic to formatting (ie. only use nxml
;;;   movement functions, never forward-line).
;;; - Real indentation function for one-entry-one-line format.
;;; - `dix-LR-restriction-copy' should work on a region of <e>'s.
;;; - `dix-expand-lemma-at-point' (either using `dix-goto-pardef' or
;;;   `lt-expand')
;;; - Some sort of interactive view of the translation process. When
;;;   looking at a word in monodix, you should easily get confirmation on
;;;   whether (and what) it is in the bidix or other monodix (possibly
;;;   just using `apertium-transfer' and `lt-proc' on the expanded
;;;   paradigm).
;;; - Function for creating a prelimenary list of bidix entries from
;;;   monodix entries, and preferably from two such lists which
;;;   we "paste" side-by-side.
;;; - `dix-LR-restriction-copy' could add a="author"
;;; - generalise to `dix-copy' with LR/RL options instead.
;;; - `dix-dixfiles' should be a list of strings instead.
;;; - `dix-sort-e-by-r' doesn't work if there's an <re> element after
;;;   the <r>; and doesn't sort correctly by <l>-element, possibly to
;;;   do with spaces
;;; - `dix-reverse' should be able to reverse on a regexp match, so
;;;   that we can do `dix-suffix-sort' by eg. <l>-elements.


(defconst dix-version "2009-07-01") 

(require 'nxml-mode)

;;;============================================================================
;;;
;;; Define the formal stuff for a minor mode named dix.
;;;

(defvar dix-mode-map (make-sparse-keymap)
  "Keymap for dix minor mode.")

(defgroup dix nil
  "Minor mode for editing Apertium XML dictionaries."
  :tag "Apertium dix"
  :group 'nxml)

;;;###autoload
(define-minor-mode dix-mode 
  "Toggle dix-mode.
With arg, turn on dix-mode if and only if arg is positive.

dix-mode is a minor mode for editing Apertium XML dictionary files.

                             KEY BINDINGS
                             ------------
\\{dix-mode-map}

Entering dix-mode calls the hook dix-mode-hook.
------------------------------------------------------------------------------"
  :init-value nil
  :lighter    " dix"
  :keymap     dix-mode-map
  :require    nxml-mode
  )

;;;============================================================================
;;;
;;; Helpers
;;;

(defmacro dix-with-sexp (&rest body)
  "Execute `body' with `nxml-sexp-element-flag' set to true."
  `(let ((old-sexp-element-flag nxml-sexp-element-flag))
     (setq nxml-sexp-element-flag t)
     ,@body
     (setq nxml-sexp-element-flag old-sexp-element-flag)))
(put 'dix-with-sexp 'lisp-indent-function 0)

(defun dix-up-to (eltname &optional barrier)
  "Move point to start of element `eltname' (a string, eg. \"e\")
which we're looking at. Optional `barrier' is the outer element,
so we don't go all the way through the file looking for our
element. Ideally `nxml-backward-up-element' should stop on
finding another `eltname' element."
  (nxml-token-after)
  (when (eq xmltok-type 'space)
    (goto-char (1+ (nxml-token-after)))
    (nxml-token-after))
  (goto-char xmltok-start)
  (let ((tok (xmltok-start-tag-qname)))
    (while (not (or (equal tok eltname)
		    (equal tok barrier)
		    (equal tok (concat "<" eltname))))
      (nxml-backward-up-element)
      (nxml-token-after)
      (setq tok (xmltok-start-tag-qname)))
    (if (equal tok barrier)
	(error "Didn't find %s" eltname))))

(defun dix-pardef-at-point (&optional clean)
  (save-excursion
    (dix-up-to "pardef" "pardefs")
    (re-search-forward "n=\"" nil t)
    (let ((pardef (symbol-name (symbol-at-point))))
      (if clean (replace-regexp-in-string
		 "\\([^/_]*\\)/?\\([^/_]*\\)__.*"
		 "\\1\\2"
		 pardef)
	pardef))))

(defun dix-lemma-at-point ()
  (save-excursion
    (dix-up-to "e" "section")
    (re-search-forward "lm=\"" nil t)
    (word-at-point)))

(defun dix-split-root-suffix ()
  (save-excursion
    (dix-up-to "e" "section")
    (nxml-down-element 2)
    (cons (word-at-point)
	  (progn
	    (nxml-up-element)
	    (re-search-forward "n=[^/]*/" nil t)
	    (word-at-point)))))

(defun dix-get-attrib (attributes name)
  "Find attribute with attribute name `name' (a string) in the
list `attributes' of the same format as
`xmltok-attributes'. Return nil if no such attribute is found."
  (if attributes
      (if (equal name (buffer-substring-no-properties
		       (xmltok-attribute-name-start (car attributes))
		       (xmltok-attribute-name-end (car attributes))))
	  (car attributes)
	(dix-get-attrib (cdr attributes) name))))

(defun dix-next-one (&optional backward)
  "Helper for `dix-next'. Todo: handle pardef entries too."

  (defun attrib-start (attributes name)
    (let ((attrib (dix-get-attrib attributes name)))
      (if attrib (xmltok-attribute-value-start attrib))))

  (defun move (spot)
    (if (if backward (< spot (point)) (> spot (point)))
	(goto-char spot)
      (progn (forward-char (if backward -1 1))
	     (dix-next-one backward))))
  
  (let* ((token-end (nxml-token-before))
	 (token-next (if backward
			 xmltok-start
		       (1+ token-end)))
	 (qname (xmltok-start-tag-qname)))
    (cond ((eq xmltok-type 'comment)
	   (goto-char token-next))

	  ((member qname '("par" "s"))
	   (move (or (attrib-start xmltok-attributes "n")
		     token-next)))

	  ((and (equal qname "e")	; monodix <e> with lm
		(attrib-start xmltok-attributes "lm"))
	   (move (attrib-start xmltok-attributes "lm")))
	  
	  ((member qname '("p" "e")) 	; bidix/pardef <e>
	   (move (point)))

	  ((memq xmltok-type '(space data end-tag))
	   (and (goto-char token-next)
		(not (and backward ; need to goto these elts from data
			  (nxml-token-before) ; before looping on:
			  (member (xmltok-start-tag-qname) '("r" "l" "i"))))
		(dix-next-one backward)))

 	  (t (move token-end)))))


(defun dix-compile-suffix-map (partype)
  "Build a hash map where keys are sorted lists of suffixes in
pardefs, eg. '(\"en\" \"ing\" \"s\"), and the value is a list of
the pardef names containing these suffixes.

Argument `partype' is eg. adj, vblex, vblex_adj, ..., and is the
string following \"__\", thus assumes you keep to the Apertium
standard. Also assumes there is no \"_\" before \"__\" in pardef
names."
  (let ((suffmap (make-hash-table :test 'equal)))
    (save-excursion
      (goto-char (point-min))
      ;; find all pardefs of `partype' in the file:
      (while (re-search-forward
	      (concat "pardef[^n>]*n=\"\\([^_]*__" partype "\\)\"") nil 'noerror)
	(let ((pardef (match-string-no-properties 1))
	      (sufflist (dix-compile-sorted-suffix-list)))
	  (puthash sufflist
		   (cons pardef (gethash sufflist suffmap)) suffmap))))
    suffmap))

(defvar dix-suffix-maps nil
  "Internal association list used to store compiled suffix maps;
keys are symbols formed from the string `partype' (see
`dix-compile-suffix-map' and interactive function
`dix-find-duplicate-pardefs').")
(make-variable-buffer-local 'dix-suffix-maps)

(defun dix-get-pardefs (sufflist suffmap)
  "Get the list of pardefs in `suffmap' which have the list of
suffixes `sufflist'. See `dix-compile-suffix-map' for more
information."
  (gethash (sort sufflist 'string-lessp) suffmap))

(defun dix-compile-sorted-suffix-list ()
  "Used for generating lookup keys for `dix-compile-suffix-map'
and `dix-get-pardefs'."
  (save-excursion
    (let (sufflist)
      (dix-up-to "pardef" "pardefs")
      ;; find all suffixes within this pardef:
      (let ((end (save-excursion (dix-with-sexp (forward-sexp))
				 (point))))
	(while (re-search-forward "<l>\\([^<]*\\)</l>" end 'noerror)
	  (when (match-string 1)
	    (setq sufflist (cons (match-string-no-properties 1) sufflist)))))
      (sort sufflist 'string-lessp))))

(defun assoc-delete-all (key alist)
  (if alist
      (if (equal (caar alist) key)
	  (assoc-delete-all key (cdr alist))
	(cons (car alist)
	      (assoc-delete-all key (cdr alist))))))

;;;============================================================================
;;;
;;; Interactive functions
;;;

(defun dix-toggle-syntax-highlighting ()
  "Toggle nXML syntax highlighting. Runs `normal-mode' to make
sure syntax highlighting gets turned on afterwards, but you'll
have to reopen the file if you want to completely clear all
syntax highlighting."
  (interactive)
  (setq nxml-syntax-highlight-flag (not nxml-syntax-highlight-flag))
  (normal-mode)
  (dix-mode 1)
  (if nxml-syntax-highlight-flag
      (message "Syntax highlighting is on")
    (message "Syntax highlighting is off")))

(defun dix-find-duplicate-pardefs (&optional recompile)
  "Find all pardefs with this list of suffixes (contents of <l>
elements); if there are several of them they might be
duplicates. Optional prefix argument `recompile' forces a
re-check of all pardefs.

Uses internal function `dix-compile-suffix-map' which assumes
that pardefs are named according to the regular Apertium scheme,
eg. \"lik/e__vblex\" (ie. all pardefs of the same group have
\"__\" before the group name, and there are no \"_\" before
\"__\").

Returns the list of pardef names."
  (interactive "P")
  (let* ((partype
	  (save-excursion
	    (dix-up-to "pardef" "pardefs")
	    (re-search-forward
	     (concat "pardef[^n>]*n=\"[^_]*__\\([^\"]*\\)" ) nil 'noerror)
	    (match-string-no-properties 1)))
	 (foundmap (cdr (assoc-string partype dix-suffix-maps))))
    (let* ((suffmap
	    (if (or recompile (not foundmap))
		(dix-compile-suffix-map partype)
	      foundmap))
	   (pardefs (dix-get-pardefs (dix-compile-sorted-suffix-list)
				     suffmap)))
      (when (or recompile (not foundmap))
	(setq dix-suffix-maps (assoc-delete-all partype dix-suffix-maps))
	(add-to-list 'dix-suffix-maps (cons partype suffmap) 'append))
      (message (prin1-to-string pardefs))
      pardefs)))

(defun dix-restriction-cycle ()
  "Cycle through possible values of the `r' attribute of the <e>
element at point."
  (interactive)
  (save-excursion
    (dix-up-to "e" "pardef")
    (let* ((old		     ; find what, if any, restriction we have:
	    (save-excursion
	      (if (re-search-forward "r=\"\\(..\\)\"" (nxml-token-after) 'noerror 1)
		  (match-string 1))))
	   (new (if old			; find our new restriction:
		    (if (equal old "LR")
			" r=\"RL\""	; "LR" => "RL"
		      "")		; "RL" =>  ""
		  " r=\"LR\"")))	;  ""  => "LR"
      ;; restrict:
      (forward-word)
      (if old (dix-with-sexp (kill-sexp)))
      (insert new)
      ;; formatting, remove whitespace:
      (forward-char) (just-one-space) (delete-backward-char 1)
      (if (equal new "") (insert "       ")))))

(defun dix-LR-restriction-copy (&optional RL)
  "Make a copy of the Apertium element we're looking at, and add
an LR restriction to the copy. A prefix argument makes it an RL
restriction."
  (interactive "P")
  (dix-up-to "e" "pardef")
  (save-excursion
    (let ((beg (point))
	  (end (1+ (nxml-scan-element-forward (point)))))
      (goto-char end)
      (insert (concat) (buffer-substring-no-properties beg end))
      (goto-char end)
      ;; restrict:
      (let ((dir (if RL "RL" "LR")))
	(forward-word) (insert " r=\"" dir "\""))
      ;; formatting, remove whitespace:
      (forward-char) (just-one-space) (delete-backward-char 1)))
  ;; move point to end of relevant word:
  (nxml-down-element 2) (when RL (nxml-forward-element))
  (nxml-down-element 1) (goto-char (nxml-token-after)))

(defun dix-RL-restriction-copy ()
  "Make a copy of the Apertium element we're looking at, and
add an RL restriction to the copy."
    (interactive)
    (dix-LR-restriction-copy 'RL))


(defun dix-sort-e-by-r (reverse beg end)
  "Sort region alphabetically by contents of <r> element;
argument means descending order. Assumes <e> elements never
occupy more than one line.

Called from a program, there
are three arguments: REVERSE (non-nil means reverse order), BEG
and END (region to sort).  The variable `sort-fold-case'
determines whether alphabetic case affects the sort order."
  (interactive "P\nr")
  (save-excursion
    (save-restriction
      (narrow-to-region beg end)
      (goto-char (point-min))
      (let ;; To make `end-of-line' and etc. to ignore fields.
	  ((inhibit-field-text-motion t))
	(sort-subr
	 reverse
	 (lambda ()	                ; nextrec
	   (goto-char (nxml-token-after)))
	 (lambda ()			; endrec
	   (dix-up-to "e")
	   (nxml-forward-element))
	 (lambda ()			; startkey
	   (nxml-down-element 2)
	   (let ((lstart (point))
		 (lend (progn (nxml-forward-element) (point)))
		 (rstart (nxml-token-after))
		 (rend (progn (nxml-forward-element) (point))))
	     (concat (buffer-substring-no-properties rstart rend)
		     (buffer-substring-no-properties lstart lend)))))))))

(defun dix-sort-pardef (reverse)
  "Sort a pardef using `dix-sort-e-by-r'."
  (interactive "P")
  (save-excursion
    (let (beg end)
      (dix-up-to "pardef" "pardefs")
      ;; get beginning of first elt within pardef:
      (setq beg (save-excursion (goto-char (nxml-token-after))
				(nxml-token-after)))
      ;; nxml-token-before is beginning of <pardef>; set xmltok-start
      ;; to beginning of </pardef>:
      (if (nxml-scan-element-forward (nxml-token-before))
	  (dix-sort-e-by-r reverse beg xmltok-start)))))

(defun dix-reverse-lines (beg end)
  "Reverse each line in the region. Used by `dix-suffix-sort'. If
called non-interactively, reverse each full line from `beg' to
`end' (inclusive, never reverses part of a line)."
  (interactive "r")
  (save-excursion
    (if (and (>= beg (line-beginning-position))
	     (<= end (line-end-position)))
	(dix-reverse (line-beginning-position)
		     (line-end-position))
      (save-restriction
	(narrow-to-region beg end)
	(goto-char (point-min))
	(while (< (point) (point-max))
	  (dix-reverse (line-beginning-position)
		       (line-end-position))
	  (forward-line))))))

(defun dix-reverse-region (beg end)
  "Reverse the text between positions `beg' and `end' in the
buffer. Used by `dix-reverse-lines'."
  (interactive "r")
  (let ((line (buffer-substring beg end)))
    (delete-region beg end)
    (insert (apply 'string (reverse (string-to-list line))))))

(defun dix-suffix-sort (beg end)
  "Sort the region by the reverse of each line, useful for
finding compound words which could have the same paradigm."
  (interactive "r")
  (dix-reverse-lines beg end)
  (sort-lines nil beg end)
  (dix-reverse-lines beg end))

(defun dix-replace-regexp-within-elt (regexp to-string eltname &optional delimited start end)
  "Does exactly what `query-replace-regexp' does, except it
restricts `regexp' and `to-string' to text within `eltname'
elements. Note: this function does not ensure that `to-string' is
free from instances of the end `eltname', so it's easy to break
if you so wish."
  (interactive
   (let ((common (query-replace-read-args
		  (if (and transient-mark-mode mark-active)
		      "Query replace regexp in region within elements" "Query replace regexp within elements")
		  t))
	 (eltname (read-from-minibuffer "Element name: ")))
     (list (nth 0 common) (nth 1 common) eltname (nth 2 common)
	   (if (and transient-mark-mode mark-active) (region-beginning))
	   (if (and transient-mark-mode mark-active) (region-end)))))
  (perform-replace (concat "\\(<" eltname ">.*\\)" regexp "\\(.*</" eltname ">\\)")
		   (concat "\\1" to-string "\\2")
		   t t delimited nil nil start end))

(defun dix-replace-regexp-within-l (regexp to-string &optional delimited start end)
  "Call `dix-replace-regexp-within-elt' on <l> elements."
  (interactive
   (let ((common (query-replace-read-args
		  (if (and transient-mark-mode mark-active)
		      "Query replace regexp in region within <l>'s" "Query replace regexp within <l>'s")
		  t)))
     (list (nth 0 common) (nth 1 common) (nth 2 common)
	   (if (and transient-mark-mode mark-active) (region-beginning))
	   (if (and transient-mark-mode mark-active) (region-end)))))
  (dix-replace-regexp-within-elt regexp to-string "l" delimited start end))

(defun dix-replace-regexp-within-r (regexp to-string &optional delimited start end)
  "Call `dix-replace-regexp-within-elt' on <r> elements."
  (interactive
   (let ((common (query-replace-read-args
		  (if (and transient-mark-mode mark-active)
		      "Query replace regexp in region within <r>'s" "Query replace regexp within <r>'s")
		  t)))
     (list (nth 0 common) (nth 1 common) (nth 2 common)
	   (if (and transient-mark-mode mark-active) (region-beginning))
	   (if (and transient-mark-mode mark-active) (region-end)))))
  (dix-replace-regexp-within-elt regexp to-string "r" delimited start end))

(defun dix-next (&optional step)
  "Moves forward `step' steps (default 1) in <e> elements between
the important places (lm attribute, <i>/<r>/<l> data, n attribute
of <par>/<s>; and then onto the next <e> element). See also
`dix-previous'."
  (interactive "p")
  (let* ((step (if step step 1))
	 (backward (< step 0)))
    (when (> (abs step) 0)
	(dix-next-one backward)
	(dix-next (if backward (1+ step) (1- step))))))

(defun dix-previous (&optional step)
  "Moves backward `step' steps (default 1) in <e> elements. See
also `dix-next'."
  (interactive "p")
  (dix-next (- (if step step 1))))

(defun dix-goto-pardef ()
  "Call from an entry to go to its pardef. Mark is pushed so you
can go back with C-u \\[set-mark-command]."
  (interactive)
  (let (pos)
    (if (save-excursion
	  (dix-up-to "e")
	  (re-search-forward "n=\"\\([^\"]*\\)\"")
	  (let ((pdname (match-string 1)))
	    (goto-char (point-min))
	    (if (re-search-forward
		 (concat "<pardef *n=\"" pdname "\"") nil t)
		(setq pos (match-beginning 0)))))
	(progn (push-mark)
	       (goto-char pos)))))

(defvar dix-modes
  '((nn-nb ("lt-proc"    "/l/n/nn-nb.automorf.bin")
	   ("cg-proc" "/l/n/nn-nb.rlx.bin")
	   ("apertium-tagger -g" "/l/n/nn-nb.prob")
	   ("apertium-pretransfer")
	   ("apertium-transfer" "/l/n/apertium-nn-nb.nn-nb.t1x" "/l/n/nn-nb.t1x.bin" "/l/n/nn-nb.autobil.bin")
	   ("lt-proc -g" "/l/n/nn-nb.autogen.bin"))
    (nb-nn ("lt-proc"    "/l/n/nb-nn.automorf.bin")
	   ("cg-proc" "/l/n/nb-nn.rlx.bin")
	   ("apertium-tagger -g" "/l/n/nb-nn.prob")
	   ("apertium-pretransfer")
	   ("apertium-transfer" "/l/n/apertium-nn-nb.nb-nn.t1x" "/l/n/nb-nn.t1x.bin" "/l/n/nb-nn.autobil.bin")
	   ("lt-proc -g" "/l/n/nb-nn.autogen.bin"))))

(defun dix-analyse (&optional no-disambiguate)
  "Very bare-bones at the moment. 

Todo: read modes.xml instead of those using those dix-path*
variables, and allow both directions (although should have some
option to override the modes.xml reading).

Todo: word-at-point function which ignores xml stuff."
  (interactive "P")
  (save-selected-window
    (let ((word (word-at-point))
	  last-output)
      (pop-to-buffer "*dix-analysis*")
      (dolist (mode dix-modes)
	(insert "==> " (symbol-name (car mode)) " <==\n")
	(setq last-output word)
	(dolist (cmd (cdr mode))
	  (let ((cmdstr (mapconcat 'concat cmd " ")))
	    (insert " " cmdstr ":\n")
	    (setq last-output
		  (substring		; trim off final newline
		   (shell-command-to-string
		    (concat "echo '" last-output "' | " cmdstr))
		   0 -1))
 	    (insert last-output "\n\n")
	    (when (and no-disambiguate (or (string= "lt-proc" (car cmd))
					   (string= "lt-proc -w" (car cmd))))
	      (insert (setq last-output (dix-analysis-split last-output)) "\n"))))))
    (nxml-mode)
    (toggle-truncate-lines 0)
    ;; ignore compiler warning, we wan to scroll here:
    (end-of-buffer)))

(defun dix-analysis-split (ambig)
  (let* ((first (string-match "/" ambig))
	 (surface (substring ambig (string-match "\\^" ambig) first))
	 (analyses (substring ambig first (string-match "\\$" ambig))))
    (mapconcat (lambda (analysis) (concat surface "/" analysis "$"))
	       (split-string analyses "/" 'omitnulls) " "))) 


;;; The following is rather nn-nb-specific stuff. Todo: generalise or remove.
(defun dix-move-to-top ()
  (interactive)
  (dix-up-to "e")
  (dix-with-sexp
   (execute-kbd-macro [?\C-\M-b ?\C-\M-f ?\C-\M-k ?\M-< ?\C-e ?\C-y ?\C-u ?\C-  ?\C-  ?\C-f])))

(defun dix-n-f-copy ()
  (interactive)
  (dix-up-to "e")
  (dix-with-sexp (mark-sexp))
  (while (re-search-forward "n=\"[mf]\"/></l>" (mark) t)
    (replace-match "n=\"f\"/></l>" t nil))
  (while (re-search-forward "n=\"[mf]\"/></r>" (mark) t)
    (replace-match "n=\"m\"/></r>" t nil))
  (dix-RL-restriction-copy)
  (dix-up-to "e")
  (dix-with-sexp (mark-sexp))
  (while (re-search-forward "n=\"[mf]\"/></r>" (mark) t)
    (replace-match "n=\"f\"/></r>" t nil))
  (dix-up-to "e")
  (dix-with-sexp (backward-sexp)))

(defcustom dix-dixfiles "*.dix dev/*dix" "String of dictionary files to grep with `dix-grep-all'"
  :type 'string
  :group 'dix)

(defun dix-grep-all ()
  "Show all usages of this pardef in the dictionaries represented
by the (customizable) string `dix-dixfiles'"
  (interactive)
  (grep (concat "grep -nH -e 'par n=\"" (dix-pardef-at-point) "' " dix-dixfiles)))

;;; Alignment ----------------------------------------------------------------
(defcustom dix-rp-align-column 28 "Column to align pardef <r> elements to with `align'"
  :type 'integer
  :group 'dix)
(defcustom dix-rb-align-column 44 "Column to align bidix <r> elements to with `align'"
  :type 'integer
  :group 'dix)
(defcustom dix-i-align-column 25 "Column to align <i> elements to with `align'"
  :type 'integer
  :group 'dix)
(defcustom dix-pp-align-column 12 "Column to align pardef <p> elements to with `align'"
  :type 'integer
  :group 'dix)
(defcustom dix-pb-align-column 10 "Column to align bidix <p> elements to with `align'"
  :type 'integer
  :group 'dix)

(defun dix-add-align-rule (name regexp column)
  (add-to-list 'align-rules-list
	       `(,name
		 (regexp . ,regexp)
		 (tab-stop . nil)
		 (spacing . 0)
		 (modes . '(nxml-mode))
		 (column . ,column))))
(add-hook
 'align-load-hook
 (lambda ()
   (dix-add-align-rule
    'dix-rp-align "^[^<].*\\S-\\(\\s-*\\)\\(<r>.*\\)$" dix-rp-align-column)
   (dix-add-align-rule
    'dix-rb-align "^<.*\\(\\s-*\\)\\(<r>.*\\)$" dix-rb-align-column)
   (dix-add-align-rule
    'dix-i-align "\\(\\s-*\\)\\(<i.*\\)$" dix-i-align-column)
   (dix-add-align-rule
    'dix-pb-align "^<.*\\(\\s-*\\)\\(<p>.*\\)$" dix-pb-align-column)
   (dix-add-align-rule
    'dix-pp-align "^[^<].*\\S-*\\(\\s-*\\)\\(<p>.*\\)$" dix-pp-align-column)))

;;; Keybindings --------------------------------------------------------------
(define-key dix-mode-map (kbd "C-c L") 'dix-LR-restriction-copy)
(define-key dix-mode-map (kbd "C-c R") 'dix-RL-restriction-copy)
(define-key dix-mode-map (kbd "<C-tab>") 'dix-restriction-cycle)
(define-key dix-mode-map (kbd "M-n") 'dix-next)
(define-key dix-mode-map (kbd "M-p") 'dix-previous)
(define-key dix-mode-map (kbd "C-c S") 'dix-sort-pardef)
(define-key dix-mode-map (kbd "C-c G") 'dix-goto-pardef)
(define-key dix-mode-map (kbd "C-c A") 'dix-grep-all)
(define-key dix-mode-map (kbd "C-c D") 'dix-find-duplicate-pardefs)
(define-key dix-mode-map (kbd "C-c C") 'dix-analyse)
(define-key dix-mode-map (kbd "C-c H") 'dix-toggle-syntax-highlighting)
(define-prefix-command 'dix-replace-prefix)
(define-key dix-mode-map (kbd "C-c %") 'dix-replace-prefix)
(define-key dix-mode-map (kbd "C-c % RET") 'dix-replace-regexp-within-elt)
(define-key dix-mode-map (kbd "C-c % %") 'dix-replace-regexp-within-elt)
(define-key dix-mode-map (kbd "C-c % l") 'dix-replace-regexp-within-l)
(define-key dix-mode-map (kbd "C-c % r") 'dix-replace-regexp-within-r)

;;; Run hooks -----------------------------------------------------------------
(run-hooks 'dix-load-hook)

(provide 'dix)

;;;============================================================================

;;; dix.el ends here
