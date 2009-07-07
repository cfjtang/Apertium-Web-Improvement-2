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
;; 	  (lambda () (if (string-match "\\.dix$" buffer-file-name) (dix-mode 1))))
;;; 
;;; `C-c L' now creates an LR-restricted copy of the <e>-element at
;;; point, `C-c R' an RL-restricted one. `C-TAB' cycles through the
;;; restriction possibilities (LR, RL, none). `C-c S' sorts a pardef,
;;; while `C-c G' moves point to the pardef of the entry at point,
;;; leaving mark where you left from. Inside a pardef, `C-c A' shows
;;; all usages of that pardef within the dictionaries represented by
;;; the string `dix-dixfiles'
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
;;;   the <r>.


(defconst dix-version "2009-07-01") 

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

(defun dix-up-to (eltname)
  "Move point before the element `eltname' (a string, eg. \"e\")
which we're looking at."
  (nxml-token-after)
  (goto-char xmltok-start)
  (let ((tok (xmltok-start-tag-qname)))
    (while (not (or (equal tok eltname)
		    (equal tok (concat "<" eltname))))
      (nxml-backward-up-element)
      (nxml-token-after)
      (setq tok (xmltok-start-tag-qname)))))

(defun dix-pardef-at-point (&optional clean)
  (save-excursion
    (dix-up-to "pardef")
    (re-search-forward "n=\"" nil t)
    (let ((pardef (symbol-name (symbol-at-point))))
      (if clean (replace-regexp-in-string
		 "\\([^/_]*\\)/?\\([^/_]*\\)__.*"
		 "\\1\\2"
		 pardef)
	pardef))))

(defun dix-lemma-at-point ()
  (save-excursion
    (dix-up-to "e")
    (re-search-forward "lm=\"" nil t)
    (word-at-point)))

(defun dix-split-root-suffix ()
  (save-excursion
    (dix-up-to "e")
    (nxml-down-element 2)
    (cons (word-at-point)
	  (progn
	    (nxml-up-element)
	    (re-search-forward "n=[^/]*/" nil t)
	    (word-at-point)))))

;;;============================================================================
;;;
;;; Interactive functions
;;;

(defun dix-restriction-cycle ()
  "Cycle through possible values of the `r' attribute of the <e>
element at point."
  (interactive)
  (save-excursion
    (dix-up-to "e")
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
  (dix-up-to "e")
  (save-excursion
    (dix-with-sexp (kill-sexp))		; todo: don't modify kill-ring
    (yank) (newline-and-indent) (yank)
    (goto-char (mark t))
    ;; restrict:
    (let ((dir (if RL "RL" "LR")))
      (forward-word) (insert (concat " r=\"" dir "\"")))
    ;; formatting, remove whitespace:
    (forward-char) (just-one-space) (delete-backward-char 1))
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
	   (nxml-forward-element)
	   (nxml-backward-down-element 2)
	   (nxml-backward-element)))))))


(defun dix-sort-pardef (reverse)
  "Sort a pardef using `dix-sort-e-by-r'."
  (interactive "P")
  (save-excursion
    (let (beg end)
      (dix-up-to "pardef")
      ;; get beginning of first elt within pardef:
      (setq beg (save-excursion (goto-char (nxml-token-after))
				(nxml-token-after)))
      ;; nxml-token-before is beginning of <pardef>; set xmltok-start
      ;; to beginning of </pardef>:
      (if (nxml-scan-element-forward (nxml-token-before))
	  (dix-sort-e-by-r reverse beg xmltok-start)))))


(defun dix-goto-pardef ()
  "Call from an entry to go to its pardef. Mark is pushed so you
can go back with C-u \\[set-mark-command]."
  (interactive)
  (if (save-excursion
	(dix-up-to "e")
	(re-search-forward "n=\"\\([^\"]*\\)\"")
	(let ((pdname (match-string 1)))
	  (goto-char (point-min))
	  (if (re-search-forward
	       (concat "<pardef *n=\"" pdname "\"") nil t)
	      (setq pos (match-beginning 0)))))
      (progn (push-mark)
	     (goto-char pos))))


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
(define-key dix-mode-map (kbd "C-c S") 'dix-sort-pardef)
(define-key dix-mode-map (kbd "C-c G") 'dix-goto-pardef)
(define-key dix-mode-map (kbd "C-c A") 'dix-grep-all)

;;; Run hooks -----------------------------------------------------------------
(run-hooks 'dix-load-hook)

(provide 'dix)

;;;============================================================================

;;; dix.el ends here
