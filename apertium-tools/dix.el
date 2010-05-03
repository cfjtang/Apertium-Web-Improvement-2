; dix.el -- minor mode for editing Apertium dictionary files

; See http://wiki.apertium.org/wiki/Emacs

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

;;; Author: Kevin Brubeck Unhammer <unhammer hos gmail punktum com>

;;; Usage:
;; (add-to-list 'load-path "/path/to/dix.el-folder") 
;; (autoload 'dix-mode "dix" 
;;   "dix-mode is a minor mode for editing Apertium XML dictionary files."  t)
;; (add-hook 'nxml-mode-hook
;; 	  (lambda () (and buffer-file-name
;; 			  (string-match "\\.dix$" buffer-file-name)
;; 			  (dix-mode 1))))
;;; 
;;; `C-c L' creates an LR-restricted copy of the <e>-element at point,
;;; `C-c R' an RL-restricted one. `C-TAB' cycles through the
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
;;       nxml-completion-hook '(rng-complete t) ; C-RET completes based on DTD
;;       rng-nxml-auto-validate-flag nil)       ; 8MB of XML takes a while
;;; You can always turn on validation again with C-c C-v. Validation
;;; is necessary for the C-RET completion, which is really handy in
;;; transfer files.
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
;;; - `dix-LR-restriction-copy' (and the other copy functions) could
;;;   add a="author"
;;; - `dix-dixfiles' should be a list of strings instead (could also
;;;   optionally auto-add all files from modes.xml?)
;;; - `dix-sort-e-by-r' doesn't work if there's an <re> element after
;;;   the <r>; and doesn't sort correctly by <l>-element, possibly to
;;;   do with spaces
;;; - `dix-reverse' should be able to reverse on a regexp match, so
;;;   that we can do `dix-suffix-sort' by eg. <l>-elements.

(defconst dix-version "2010-04-06") 

(require 'nxml-mode)
(require 'easymenu)

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
;;; Menu
;;;

(easy-menu-define dix-mode-easy-menu dix-mode-map "dix-mode menu" 
  '("dix"
    ["View pardef" dix-view-pardef
     :help "View the pardef in another window"]
    ["Go to pardef" dix-goto-pardef]
    "---"
    ["Sort pardef" dix-sort-pardef
     :help "Must be called from within a pardef"]
    ["Grep for this pardef in dix-dixfiles" dix-grep-all
     :help "Must be called from within a pardef. Uses the variable dix-dixfiles"]
    ["Show Duplicate pardefs" dix-find-duplicate-pardefs
     :help "Must be called from within a pardef. Calculate must have been called at least once"]
    ["Calculate and Show Duplicate pardefs" (dix-find-duplicate-pardefs 'recompile)
     :keys "C-u C-c D"
     :help "Must be called from within a pardef. Slower, but must be called at least once before showing duplicate pardefs"]
    "---"
    ["Narrow Buffer to Given sdef" dix-narrow-to-sdef
     :help "Show only that part of the buffer which contains a given sdef, eg. work only on nouns for a while. Widen with `C-x n w' as per usual."]
    "---"
    ["Change Restriction of <e> (LR, RL, none)" dix-restriction-cycle]
    ["Go to Next Useful Position in the Buffer" dix-next]
    ["Go to Previous Useful Position in the Buffer" dix-previous]
    ("Replace Regexp Within..."
     ["Certain Elements" dix-replace-regexp-within-elt
      :help "Prompts for an element name"]
     ["<l> Elements" dix-replace-regexp-within-l]
     ["<r> Elements" dix-replace-regexp-within-r])
    ("Copy <e> and..."
     ["Keep Contents" dix-copy
      :help "Make a copy of the current <e> element"]
     ["Apply an LR Restriction" dix-LR-restriction-copy
      :help "Make a copy of the current <e> element"]
     ["Apply an RL Restriction" dix-RL-restriction-copy
      :help "Make a copy of the current <e> element"]
     ["Increase slr sense index" dix-slr-copy
      :help "Make a copy of the current <e> element"]
     ["Increase srl sense index" dix-srl-copy
      :help "Make a copy of the current <e> element"]
     ["Clear Contents" (dix-copy 'remove-lex)
      :keys "C-u C-c C"
      :help "Make a copy of the current <e> element"]
     ["Prepend kill-buffer into lm and <i>" dix-copy-yank
      :help "Make a copy of the current <e> element"])
    ["I-search Within lm's (rather buggy)" dix-word-search-forward]
    "---"
    ["Syntax Highlighting" dix-toggle-syntax-highlighting
     :style toggle
     :selected nxml-syntax-highlight-flag
     :help "Toggle syntax highlighting of unseen parts of the buffer"]
    ["Customize dix-mode" (customize-group 'dix)]
    ["Help for dix-mode" (describe-function 'dix-mode)
     :keys "C-h m"]
    ["Show dix-mode Version" (message "dix-mode version %s" dix-version)]))

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
(defmacro dix-with-no-case-fold (&rest body)
  "Execute `body' with `case-fold-search' set to nil."
  `(let ((old-case-fold-search case-fold-search))
     (setq case-fold-search nil)
     ,@body
     (setq case-fold-search old-case-fold-search)))
(put 'dix-with-sexp 'lisp-indent-function 0)
(put 'dix-with-no-case-fold 'lisp-indent-function 0)

(defvar dix-parse-bound 5000
  "Relative bound; maximum amount of chars (not lines) to parse
through in dix xml operations (since dix tend to get
huge). Decrease the number if operations ending in \"No parent
element\" take too long.")

(defun dix-backward-up-element (&optional arg bound)
  "Modified from `nxml-backward-up-element' to include optional
arg `bound'"
  (interactive "p")
  (or arg (setq arg 1))
  (if (< arg 0)
      (nxml-up-element (- arg))
    (condition-case err
	(while (and (> arg 0)
		    (< (point-min) (point)))
	  (let ((token-end (nxml-token-before)))
	    (goto-char (cond ((or (memq xmltok-type '(start-tag
						      partial-start-tag))
				  (and (memq xmltok-type
					     '(empty-element
					       partial-empty-element))
				       (< (point) token-end)))
			      xmltok-start)
			     ((nxml-scan-element-backward
			       (if (and (eq xmltok-type 'end-tag)
					(= (point) token-end))
				   token-end
				 xmltok-start)
			       t
			       bound)
			      xmltok-start)
			     (t (error "No parent element")))))
	  (setq arg (1- arg)))
      (nxml-scan-error
       (goto-char (cadr err))
       (apply 'error (cddr err))))))

(defun dix-up-to (eltname &optional barrier)
  "Move point to start of element `eltname' (a string, eg. \"e\")
which we're looking at. Optional `barrier' is the outer element,
so we don't go all the way through the file looking for our
element (ultimately constrained by the variable
`dix-parse-bound').  Ideally `dix-backward-up-element' should
stop on finding another `eltname' element."
  (nxml-token-after)
  (when (eq xmltok-type 'space)
    (goto-char (1+ (nxml-token-after)))
    (nxml-token-after))
  (goto-char xmltok-start)
  (let ((tok (xmltok-start-tag-qname))
	(bound (max (point-min)
		    (- (point) dix-parse-bound))))
    (while (not (or (equal tok eltname)
		    (equal tok barrier)
		    (equal tok (concat "<" eltname))))
      (dix-backward-up-element 1 bound)
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
  (if (string-match "-[^.]+\\.[^.]+$" (buffer-file-name))
      (word-at-point) ;; bidix
    (save-excursion   ;; monodix
      (dix-up-to "e" "section")
      (re-search-forward "lm=\"" nil t)
      (word-at-point))))

(defun dix-pardef-type-of-e ()
  (save-excursion
    (dix-up-to "e" "section")
    (nxml-down-element 1)
    (re-search-forward "n=[^_]*__\\([^\"]*\\)" nil t)
    (match-string-no-properties 1)))

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
      (condition-case nil
	  (progn (dix-up-to "pardef" "pardefs"))
	(error (dix-goto-pardef)))
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
	    (condition-case nil
		(progn (dix-up-to "pardef" "pardefs"))
	      (error (dix-goto-pardef)))
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

(defun dix-restriction-cycle (&optional dir)
  "Cycle through possible values of the `r' attribute of the <e>
element at point. Optional argument `dir' is a string, either
\"\", \"LR\" or \"RL\"."
  (interactive)
  (save-excursion
    (dix-up-to "e" "pardef")
    (let* ((old		     ; find what, if any, restriction we have:
	    (save-excursion
	      (if (re-search-forward " r=\"\\(..\\)\"" (nxml-token-after) 'noerror 1)
		  (match-string 1))))
	   (dir (if dir dir
		  (if old		; find our new restriction:
		      (if (equal old "LR")
			  "RL"	; "LR" => "RL"
			"")	; "RL" =>  ""
		    "LR")))	;  ""  => "LR"
	   (new (if (equal dir "") ""
		  (concat " r=\"" dir "\""))))
      ;; restrict:
      (forward-word)
      (if old (delete-region (match-beginning 0)
			     (match-end 0)))
      (insert new)
      (unless (looking-at ">") (just-one-space))
      ;; formatting, remove whitespace:
      (goto-char (nxml-token-after))
      (unless (looking-at "<")
	(goto-char (nxml-token-after)))
      (delete-horizontal-space)
      (cond  ((looking-at "<i") (indent-to dix-i-align-column))
	     ((save-excursion (search-forward "</pardef>" nil 'noerror 1))
	      (indent-to dix-pp-align-column))
	     ((looking-at "<p") (indent-to dix-pb-align-column))))))

(defun dix-LR-restriction-copy (&optional RL)
  "Make a copy of the Apertium element we're looking at, and add
an LR restriction to the copy. A prefix argument makes it an RL
restriction."
  (interactive "P")
  (save-excursion
    (dix-copy)
    (let ((dir (if RL "RL" "LR")))
      (dix-restriction-cycle dir)))
  ;; move point to end of relevant word:
  (dix-up-to "e" "pardef")
  (nxml-down-element 2) (when RL (nxml-forward-element))
  (nxml-down-element 1) (goto-char (nxml-token-after)))

(defun dix-RL-restriction-copy ()
  "Make a copy of the Apertium element we're looking at, and
add an RL restriction to the copy."
  (interactive)
  (dix-LR-restriction-copy 'RL))

(defun dix-copy (&optional remove-lex)
  "Make a copy of the Apertium element we're looking at. Optional
prefix argument `remove-lex' removes the contents of the lm
attribute and <i> or <p> elements."
  (interactive "P")
  ;; todo: find the first one of these: list-item, e, def-var, sdef, attr-item, cat-item, clip, pattern-item, 
  (dix-up-to "e" "pardef")
  (let ((beg (or (re-search-backward "^[\t ]*" (line-beginning-position) 'noerror) (point)))
	(end (1+ (save-excursion
		   (goto-char (nxml-scan-element-forward (point)))
		   (or (re-search-forward "[\t ]*$" (line-end-position) 'noerror) (point))))))
    (goto-char end)
    (insert (buffer-substring-no-properties beg end))
    (let ((copyend (point)))
      (goto-char end)
      (when (save-excursion (search-forward "</pardef>" nil 'noerror 1))
	(indent-to dix-ep-align-column))
      (when remove-lex
	(save-excursion
	  (save-restriction
	    (narrow-to-region end copyend)
	    (while (re-search-forward "lm=\\\"[^\\\"]*\\\"" nil 'noerror 1)
	      (replace-match "lm=\"\""))
	    (goto-char (point-min))
	    (while (re-search-forward "<i>.*</i>" nil 'noerror 1)
	      (replace-match "<i></i>"))
	    (goto-char (point-min))
	    (while (re-search-forward "<p>.*</p>" nil 'noerror 1)
	      (replace-match "<p><l></l><r></r></p>"))))))))



(defun dix-copy-yank ()
  "Make a copy of the Apertium element we're looking at, and yank
into the beginning of the lm and <i>."
  (interactive)
  (dix-copy)
  (dix-next 1)
  (yank)
  (dix-next 1)
  (yank))

(defun dix-increase-sense (&optional dir)
  (interactive)
  (let ((dir (or dir "slr")))
    (save-excursion
      (dix-up-to "e" "section")
      (let* ((old	     ; find what, if any, restriction we have:
	      (save-excursion
		(if (re-search-forward (concat " " dir "=\"\\([0-9]+\\)\"") (nxml-token-after) 'noerror)
		    (match-string 1))))
	     (new (number-to-string (if old (1+ (string-to-number old)) 1))))
	;; restrict:
	(forward-word)
	(if old (delete-region (match-beginning 0)
			       (match-end 0)))
	(insert (concat " " dir "=\"" new "\""))
	(unless (looking-at ">") (just-one-space))
	;; formatting, remove whitespace:
	(goto-char (nxml-token-after))
	(unless (looking-at "<") (goto-char (nxml-token-after)))
	(delete-horizontal-space)
	(cond ((looking-at "<i") (indent-to dix-i-align-column))
	      ((looking-at "<p") (indent-to dix-pb-align-column)))))))

(defun dix-slr-copy (&optional srl)
  "Make a copy of the Apertium element we're looking at, and
increase the slr sense attribute of the copy (optionally adding
it if it's not present). Optional prefix argument `srl' makes it
an srl sense."
  (interactive "P")
  (let ((dir (if srl "srl" "slr")))
    (save-excursion
      (dix-copy)
      (dix-increase-sense dir)))
  ;; move point to end of relevant word:
  (dix-up-to "e" "section")
  (nxml-forward-element)
  (nxml-down-element 2) (unless srl (nxml-forward-element))
  (nxml-down-element 1) (goto-char (nxml-token-after)))

(defun dix-srl-copy ()
  "Make a copy of the Apertium element we're looking at, and
increase the srl sense attribute of the copy (optionally adding
it if it's not present)."
  (interactive)
  (dix-slr-copy 'srl))


(defun dix-sort-e-by-l (reverse beg end &optional by-r)
  "Sort region alphabetically by contents of <l> element (or by
<r> element if optional argument `by-r' is true); argument means
descending order. Assumes <e> elements never occupy more than one
line.

Called from a program, there are three arguments:
`reverse' (non-nil means reverse order), `beg' and `end' (region
to sort).  The variable `sort-fold-case' determines whether
alphabetic case affects the sort order."
  (interactive "P\nr")
  (save-excursion
    (save-restriction
      (narrow-to-region beg end)
      (goto-char (point-min))
      (let ;; make `end-of-line' and etc. to ignore fields:
	  ((inhibit-field-text-motion t))
	(sort-subr
	 reverse
	 (lambda ()	                ; nextrec
	   (goto-char (nxml-token-after))
	   (re-search-backward "\\s *" (line-beginning-position) 'noerror))
	 (lambda ()			; endrec
	   (dix-up-to "e")
	   (nxml-forward-element)
	   (re-search-forward "\\s *" (line-end-position) 'noerror)
	   (let ((next-tok (nxml-token-after))) ; skip comments:
	     (while (and (eq xmltok-type 'comment)
			 (<= next-tok (line-end-position)))
	       (goto-char next-tok)
	       (re-search-forward "\\s *" (line-end-position) 'noerror)
	       (setq next-tok (nxml-token-after)))))
	 (lambda ()			; startkey
	   (nxml-down-element 2)
	   (let* ((lstart (point))
		  (lend (progn (nxml-forward-element) (point)))
		  (rstart (if (looking-at "<") (point) (nxml-token-after)))
		  (rend (progn (nxml-forward-element) (point)))
		  (l (buffer-substring-no-properties lstart lend))
		  (r (buffer-substring-no-properties rstart rend)))
	     (if by-r
		 (concat r l)
	       (concat l r)))))))))

(defun dix-sort-e-by-r (reverse beg end)
  (interactive "P\nr")
  (dix-sort-e-by-l reverse beg end 'by-r))

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
	(dix-reverse-region (line-beginning-position)
			    (line-end-position))
      (save-restriction
	(narrow-to-region beg end)
	(goto-char (point-min))
	(while (< (point) (point-max))
	  (dix-reverse-region (line-beginning-position)
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


(defvar dix-search-substring nil
  "Set by `dix-word-search-forward'.")

(defun dix-word-search-forward (&optional substring)
  "Incremental word-search for dix files. In monodix, searches
only within lm attributes, in bidix, searches only between > and
< symbols. Optional prefix argument `substring' lets you search
for parts of words, otherwise you have to type the whole word in
to get a (correct) hit.

Todo: 
- make some way of searching for prefixes or suffixes (ie. so
  that the .* is only put at the end or beginning)
- on failing search, don't mark anything (currently, it marks
  until end-of-buffer...)
- unless we're doing a substring search, we should probably not
  do an incremental search (that is, not search until user
  presses enter), but word-search-forward isn't good
  enough (doesn't highlight, no way to C-s to the next hit...)
- turn spaces into <b/> (or space|<b/> in monodix)"
  (interactive "P")
  (setq dix-search-substring substring)
  (isearch-mode
   'forward 'regexp 
   (lambda ()
     (let* ((bidix (string-match "\\...*-..*\\.dix" (buffer-file-name)))
	    (l (if bidix ">" "lm=\""))
	    (r (if bidix "<" "\""))
	    (affix (if dix-search-substring (concat "[^" r "]*"))))
       (setq isearch-string
	     (concat l affix isearch-message affix r))
       (goto-char isearch-opoint)
       (setq isearch-forward t)	; o/w the first C-s goes backward, for some reason
       (isearch-search)
       (isearch-update)))))


(defun dix-find-rhs-mismatch ()
  "Find possible mismatches in <r> elements (ie. a pardef where
two <e>'s have different suffixes in their <r>'s).

Only stops at the first mismatch within one pardef."
  (interactive)
  (defun next-pardef ()
    (and (search-forward "pardef" nil t) (next-rhs)))
  (defun next-rhs ()
    (re-search-forward "<r>\\([^<]*\\)<\\|\\(</pardef>\\)" nil t))
  (let ((keep-looking (next-pardef))	; find first hit
	(last-rhs (match-string 1)))
    ;; Check next ones for mismatches:
    (while keep-looking
      (if (equal (match-string 2) "</pardef>")
	  (setq keep-looking (next-pardef) ; skip to next <pardef>
		last-rhs (match-string 1))
	(if (equal (match-string 1) last-rhs)
	    (next-rhs)			; skip to next <e>
	  (setq keep-looking nil))))
    ;; Echo results:
    (if (match-string 1)
	(and (goto-char (match-end 1))
	     (message
	      (concat "Possible mismatch in <r>: " last-rhs " vs " (match-string 1))))
      (message "No mismatches discovered."))))

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

(defun dix-nearest-pdname (origin)
  "Return the pardef-name nearest `origin' within an <e> element."
  (save-excursion
    (dix-up-to "e")
    (let* ((e-end (nxml-scan-element-forward (nxml-token-before)))
	   (pdname (and (re-search-forward "par\\s *n=\"\\([^\"]*\\)\"" e-end)
			(match-string-no-properties 1))))
      (while (and (re-search-forward "<par\\s *n=\"\\([^\"]*\\)\"" e-end 'noerror)
		  (< (match-beginning 0) origin))
	(setq pdname (match-string-no-properties 1)))
      pdname)))

(defun dix-goto-pardef ()
  "Call from an entry to go to its pardef. Mark is pushed so you
can go back with C-u \\[set-mark-command]."
  (interactive)
  (let ((start (point))
	pos)
    (if (save-excursion
	  (let* ((pdname (dix-nearest-pdname start)))
	    (goto-char (point-min))
	    (if (re-search-forward
		 (concat "<pardef *n=\"" pdname "\"") nil t)
		(setq pos (match-beginning 0)))))
	(progn (push-mark)
	       (goto-char pos)))))

(defun dix-view-pardef ()
  "Show pardef in other window. Unfortunately, I haven't found
out how to have two different buffer restrictions going at once,
the pardef is just inserted into a new buffer where you can
eg. edit at will and then paste back. The nice thing is that for
each call of this function, the pardef is added to the
*dix-view-pardef* buffer, so you get a temp buffer where you can
eg. collapse pardefs."
  (interactive)
  (save-excursion
    (save-restriction
      (widen)
      (dix-goto-pardef)
      (let* ((beg (point))
	     (end (1+ (nxml-scan-element-forward beg)))
	     (pardef (buffer-substring beg end)))
	(save-selected-window
	  (pop-to-buffer "*dix-view-pardef*")
	  (insert pardef)    
	  (nxml-mode)
	  (dix-mode 1)
	  (goto-char (point-max))	; from `end-of-buffer'
	  (overlay-recenter (point))
	  (recenter -3))))))  

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
    (goto-char (point-max))		; from `end-of-buffer'
    (overlay-recenter (point))
    (recenter -3)))

(defun dix-analysis-split (ambig)
  (let* ((first (string-match "/" ambig))
	 (surface (substring ambig (string-match "\\^" ambig) first))
	 (analyses (substring ambig first (string-match "\\$" ambig))))
    (mapconcat (lambda (analysis) (concat surface "/" analysis "$"))
	       (split-string analyses "/" 'omitnulls) " "))) 

(defun dix-point-after-> ()
  (equal (buffer-substring-no-properties (1- (point)) (point))
	 ">"))

(defun dix-space ()
  "This should insert a space, unless we're inside the data area
of <g>, <r>, <l> or <i>, in which case we want a <b/>. If we're
in the attribute of a <par> or <pardef>, we insert an underscore.

A bit hacky I guess, but I don't want to require nxhtml just to
get nxml-where-path, and reimplementing an XML Path seems rather
too much work for this."
  (interactive)
  
  (defun in-elt (names)	; nxml-token-before must be called before this
    (let ((eltname (save-excursion
		     (goto-char xmltok-start)
		     (when (equal xmltok-type 'data)
		       (nxml-token-before)
		       (goto-char xmltok-start))
		     (xmltok-start-tag-qname))))
      (and eltname (member eltname names))))
  
  (nxml-token-before)
  (cond ((and (or (eq xmltok-type 'data)
		  (and (memq xmltok-type '(start-tag empty-element))
		       (dix-point-after->)))
	      (in-elt '("g" "b" "r" "l" "i")))
	 (insert "<b/>"))
	((and (catch 'in-attr
		(dolist (attr xmltok-attributes)
		  (if (and (xmltok-attribute-value-start attr)
			   (>= (point) (xmltok-attribute-value-start attr))
			   (xmltok-attribute-value-end   attr)
			   (<= (point) (xmltok-attribute-value-end   attr))
			   (equal (xmltok-attribute-local-name attr) "n"))
		      (throw 'in-attr t))))
	      (in-elt '("par" "pardef")))
	 (insert "_"))
	(t 
	 (insert " "))))

(defcustom dix-hungry-backspace nil
  "Delete whole XML elements (<b/>, comments) with a single press
of the backspace key. Set to nil if you don't want this behaviour."
  :type 'boolean
  :group 'dix)

(defun dix-backspace ()
  "Delete a character backward, unless we're looking at the end
of <b/> or a comment, in which case we delete the whole element.

Note: if we're looking at the relevant elements, prefix arguments
are ignored, while if we're not, a prefix argument will be passed
to the regular `delete-backward-char'."
  (interactive)
  (if (and dix-hungry-backspace
	   (nxml-token-before)
	   (or
	    (and (eq xmltok-type 'empty-element)
		 (equal (xmltok-start-tag-qname) "b"))
	    (and (eq xmltok-type 'comment)
		 (dix-point-after->))))
      (delete-region (point) xmltok-start)
    (call-interactively 'delete-backward-char)))

(defun dix-< (literal)			; not in use yet
  "Inserts < in space or unclosed tags, otherwise moves to the
beginning of the element."
  (interactive "*P")
  (if literal
      (self-insert-command (prefix-numeric-value literal))
    (nxml-token-after)
    (cond ((memq xmltok-type '(space data not-well-formed partial-start-tag))
	   (insert-char ?< 1))
	  (t (progn (nxml-up-element)
		    (dix-with-sexp (backward-sexp)))))))
(defun dix-> (literal)
  "Inserts > in space or unclosed tags, otherwise moves to the
end of the element."
  (interactive "*P")
  (if literal
      (self-insert-command (prefix-numeric-value literal))
    (nxml-token-before)
    (cond ((memq xmltok-type '(space not-well-formed partial-start-tag))
	   (insert-char ?> 1))
	  (t (nxml-up-element)))))

(defun dix-trim-string (s)
  "Trim leading and trailing spaces, tabs and newlines off `s'."
  (cond ((not (stringp s)) nil)
	((string-match "\\([^ \t\n]+\\)" s) (match-string 1 s))
	(t s)))

(defvar dix-expansion-left nil
  "Cons of the tmpfile used to store the expansion, and the
timestamp of the file last time we expanded it, as given
by (sixth (file-attributes dix-monodix-left)).")
(defvar dix-monodix-left "/l/n/apertium-nn-nb.nn.dix")

(defun dix-expansion-sentinel (proc change-str)
  (when (string= "finished\n" change-str)
    (message "Expansion updated")
    (kill-buffer "*lt-expand*")))

(defun dix-update-expansion (expansion monodix update-expansion)
  "Due to emacs variable scoping, we have to include a function
`update-expansion' that updates the `expansion' variable with a
new tmpfile and `monodix' timestamp."
  (if (or (not expansion)
	  (and expansion
	       (not (equal (sixth (file-attributes monodix))
			   (cdr expansion)))))
      (let ((tmpfile (dix-trim-string
		      (shell-command-to-string "mktemp -t expansion.XXXXXXXXXX"))))
	(message "Expansion out of date with left monodix, hang on...")
	(if (and (file-attributes tmpfile) (file-attributes monodix))
	    (progn
	      (shell-command
	       (concat "lt-expand " monodix " " tmpfile " &") "*lt-expand*")
	      (funcall update-expansion tmpfile (sixth (file-attributes monodix)))
	      (set-process-sentinel (get-buffer-process "*lt-expand*") #'dix-expansion-sentinel))
	  (error "mktemp command failed: %s" tmpfile)))
    (message "Expansion up-to-date")))

(defun dix-update-expansions ()
  (interactive)
  (dix-update-expansion dix-expansion-left dix-monodix-left
			(lambda (file time)
			  (setq dix-expansion-left (cons file time)))))

(defun dix-expand (lemma pos)
  (shell-command (concat "grep ':\\([>]:\\)*" lemma "<" pos ">' " (car dix-expansion-left))))
(defun dix-expand-possibilities (tags)
  "There should be no leading < in `tags', but you can have
several with >< between them."
  (shell-command (concat "grep ':\\([>]:\\)*[^<]*<" tags "' " (car dix-expansion-left)
			 " | sed 's/.*:[^\\<]*\\</</' | sort | uniq")))

(defun dix-narrow-to-sdef-narrow (sdef sec-start sec-end)
  (goto-char sec-start)
  (search-forward (concat "<s n=\"" sdef "\""))
  (search-backward "<e")
  (beginning-of-line)
  (narrow-to-region (point)
		    (save-excursion
		      (goto-char sec-end)
		      (search-backward (concat "<s n=\"" sdef "\""))
		      (search-forward "</e")
		      (end-of-line)
		      (point))))

(defun dix-narrow-to-sdef (&optional no-widen)
  "Narrow buffer to a region between the first and the last
occurence of a given sdef in a given section; lets you
tab-complete on sdefs and sections.

Optional prefix argument `no-widen' lets you narrow even more in
on a previously narrowed buffer (the default behaviour for
`narrow-to-region'), otherwise the buffer is widened first."
  (interactive "P")
  (dix-with-no-case-fold
   (let (sdefs)
     (save-excursion ;; find all sdefs
       (save-restriction (widen)
	 (goto-char (point-min))
	 (while (re-search-forward
		 "<sdef[^>]*n=\"\\([^\"]*\\)\"" nil 'noerror)
	   (add-to-list 'sdefs (match-string-no-properties 1)))))
     (let ((sdef (completing-read "sdef/POS-tag: " sdefs nil 'require-match))
	   id start end sections)
       (save-excursion ;; find all sections
	 (save-restriction (widen)
	   (goto-char (point-min))
	   (while (setq start (re-search-forward
			       "<section[^>]*id=\"\\([^\"]*\\)\"" nil 'noerror))
	     (setq id (match-string-no-properties 1))
	     (setq end (re-search-forward "</section>"))
	     (if (search-backward (concat "<s n=\"" sdef "\"") start 'noerror)
		 (add-to-list 'sections (list id start end))))))
       ;; narrow to region between first and last occurrence of sdef in chosen section
       (let* ((ids (mapcar 'car sections))
	      (id (completing-read "Section:" ids nil 'require-match
				   (if (cdr ids) nil (car ids))))
	      (section (assoc id sections)))
	 (unless no-widen (widen))
	 (dix-narrow-to-sdef-narrow sdef (second section) (third section)))))))

;;; The following is rather nn-nb-specific stuff. Todo: generalise or remove.
(defun dix-move-to-top ()
  (interactive)
  (save-excursion
    (dix-up-to "e")
    (when (re-search-backward "\\S " nil t)
      (forward-char))
    (let* ((beg (point))
	   (end (nxml-scan-element-forward beg))
	   (elt (buffer-substring beg end)))
      (delete-region beg end)
      (beginning-of-buffer)
      (end-of-line)
      (insert elt)))
  (re-search-forward "\\S "))

(defcustom dix-dixfiles "*.dix dev/*dix" "String of dictionary files to grep with `dix-grep-all'"
  :type 'string
  :group 'dix)

(defun dix-grep-all ()
  "Show all usages of this pardef in the dictionaries represented
by the (customizable) string `dix-dixfiles'"
  (interactive)
  (grep (concat "grep -nH -e 'par n=\"" (dix-pardef-at-point) "\"' " dix-dixfiles)))

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
(defcustom dix-ep-align-column 2 "Column to align pardef <e> elements to with `align'.
Not yet implemented, only used by `dix-LR-restriction-copy'."
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
(define-prefix-command 'dix-sense-prefix)
(define-key dix-mode-map (kbd "C-c s") 'dix-sense-prefix)
(define-key dix-mode-map (kbd "C-c s l") 'dix-slr-copy)
(define-key dix-mode-map (kbd "C-c s r") 'dix-srl-copy)
(define-key dix-mode-map (kbd "C-c C") 'dix-copy)
(define-key dix-mode-map (kbd "C-c C-y") 'dix-copy-yank)
(define-key dix-mode-map (kbd "<C-tab>") 'dix-restriction-cycle)
(define-key dix-mode-map (kbd "M-n") 'dix-next)
(define-key dix-mode-map (kbd "M-p") 'dix-previous)
(define-key dix-mode-map (kbd "C-c S") 'dix-sort-pardef)
(define-key dix-mode-map (kbd "C-c G") 'dix-goto-pardef)
(define-key dix-mode-map (kbd "C-c V") 'dix-view-pardef)
(define-key dix-mode-map (kbd "C-c W") 'dix-word-search-forward)
(define-key dix-mode-map (kbd "C-c A") 'dix-grep-all)
(define-key dix-mode-map (kbd "C-c D") 'dix-find-duplicate-pardefs)
(define-key dix-mode-map (kbd "C-c C-c") 'dix-analyse)
(define-key dix-mode-map (kbd "C-c H") 'dix-toggle-syntax-highlighting)
(define-prefix-command 'dix-replace-prefix)
(define-key dix-mode-map (kbd "C-c %") 'dix-replace-prefix)
(define-key dix-mode-map (kbd "<SPC>") 'dix-space)
(define-key dix-mode-map (kbd "<backspace>") 'dix-backspace)
(define-key dix-mode-map (kbd "C-c % RET") 'dix-replace-regexp-within-elt)
(define-key dix-mode-map (kbd "C-c % %") 'dix-replace-regexp-within-elt)
(define-key dix-mode-map (kbd "C-c % l") 'dix-replace-regexp-within-l)
(define-key dix-mode-map (kbd "C-c % r") 'dix-replace-regexp-within-r)
(define-key dix-mode-map (kbd "C-<") 'dix-<)
(define-key dix-mode-map (kbd "C->") 'dix->)
(define-key dix-mode-map (kbd "C-x n s") 'dix-narrow-to-sdef)

;;; Run hooks -----------------------------------------------------------------
(run-hooks 'dix-load-hook)

(provide 'dix)

;;;============================================================================

;;; dix.el ends here
