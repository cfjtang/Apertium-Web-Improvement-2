;;; init-apertium.el --- Set up all apertium-related packages

;; Copyright (C) 2016 Kevin Brubeck Unhammer

;; Author: Kevin Brubeck Unhammer <unhammer@fsfe.org>
;; Version: 0.1.0
;; Url: http://wiki.apertium.org/wiki/Emacs
;; Keywords: languages

;; This file is not part of GNU Emacs.

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
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

;;; Commentary:

;; Usage:

;; Put
;;
;; (load "/path/to/init-apertium.el")
;;
;; in your ~/.emacs.d/init.el and (re)start Emacs.
;;
;; The first time it start up, it should install some packages; if you
;; later want to update them, you can do `M-x list-packages' and then
;; press `U x' to mark upgradables and upgrade them.
;;
;; Note that some of the .dix validation functions can be slow on old
;; computers; if editing large .dix files seems too slow, try adding
;; turning off one or both of the validators with by putting
;;
;; (add-hook 'nxml-mode-hook (lambda () (rng-validate-mode 0)) 'append)
;; (add-hook 'dix-mode-hook (lambda () (flycheck-mode 0)) 'append)
;;
;; in your ~/.emacs.d/init.el


;; TODO:
;; - yasnippet (but dix-yas needs some work before really useful)


;;; Code:


;; Temporarily high, for slighly faster startup:
(setq gc-cons-threshold (* 100 1024 1024))

;; Remove some waste of space:
(when (fboundp 'tool-bar-mode)
  (tool-bar-mode -1))
;; Power users might also want:
; (when (fboundp 'menu-bar-mode)
;   (menu-bar-mode -1))
; (when (fboundp 'scroll-bar-mode)
;   (scroll-bar-mode -1))

(setq inhibit-splash-screen t
      load-prefer-newer t)


(setq safe-local-variable-values
      (append safe-local-variable-values
              '((cg-post-pipe . "cg-conv --out-apertium")
                (cg-pre-pipe . "apertium -d . dan-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . dan-nno-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . dan-nno-morph|cg-conv -a")
                (cg-pre-pipe . "apertium -d . dan-nor-morph|cg-conv -a")
                (cg-pre-pipe . "apertium -d . nno-dan-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . nno-dan-morph|cg-conv -a")
                (cg-pre-pipe . "apertium -d . nno-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . nob-dan-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . nob-dan-morph|cg-conv -a")
                (cg-pre-pipe . "apertium -d . nob-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . sco-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . sme-nob-biltrans | cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . sme-smj-biltrans|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "apertium -d . swe-morph|cg-conv -a 2>/dev/null")
                (cg-pre-pipe . "cat")
                (cg-pre-pipe . "hfst-proc -w -p sme-nob.automorf.hfst |cg-conv -a"))))


(if (locate-library "package")
    (eval-and-compile
      (setq package-enable-at-startup nil
            package-archives '(("gnu" . "https://elpa.gnu.org/packages/")
                               ("melpa-stable" . "https://stable.melpa.org/packages/")
                               ("melpa" . "https://melpa.org/packages/"))
            ;; This should prefer melpa-stable over melpa:
            use-package-always-pin "melpa-stable")

      (require 'package)
      (package-initialize)
      (unless (package-installed-p 'use-package)
        (unless (assoc 'use-package package-archive-contents)
          (package-refresh-contents))
        (package-install 'use-package))
      (require 'use-package))
  (message "WARNING: Ancient emacs! Apertium packages won't work without package.el")
  (defmacro use-package (&rest body) "Ignores BODY -- this emacs is too old."))

;; From https://github.com/jwiegley/use-package/issues/338 -- keep
;; going even if a package failed to install:
(defadvice use-package-ensure-elpa (around use-package-ensure-safe activate)
  "Capture errors from installing packages."
  (condition-case-unless-debug err
      ad-do-it
    (error
     (ignore
      (display-warning 'use-package
                       (format "Failed to install %s: %s"
                               package (error-message-string err))
                       :error)))))



(use-package dix
  :defer t
  :ensure t
  :commands dix-mode
  :functions (dix-C-c-letter-keybindings)
  :mode (("\\.dix\\'" . nxml-mode)
         ("\\.t[0-9s]x\\'" . nxml-mode)
         ("\\.lrx\\'" . nxml-mode)
         ("\\.metalrx\\'" . nxml-mode)
         ("\\.metadix\\'" . nxml-mode)
         ("\\.multidix\\'" . nxml-mode))
  :init
  (setq rng-nxml-auto-validate-flag nil)
  (add-hook 'nxml-mode-hook
            (defun dix-on-nxml-mode ()
              (modify-syntax-entry ?> ")<" nxml-mode-syntax-table)
              (modify-syntax-entry ?< "(>" nxml-mode-syntax-table)
              (and (buffer-file-name)
                   (string-match "\\.\\(meta\\|multi\\)?dix$\\|\\.t[0-9s]x$\\|\\.lrx$\\|\\.metalrx$\\|/modes\\.xml$\\|/cross-model\\.xml$" buffer-file-name)
                   (dix-mode 1))
              (unless (and (buffer-file-name)
                           (string-match "\\.\\(meta\\|multi\\)?dix$" buffer-file-name))
                (rng-validate-mode 1))))
  (add-hook 'dix-mode-hook #'dix-C-c-letter-keybindings)
  (setq dix-hungry-backspace t)
  :config
  (add-to-list 'nxml-completion-hook 'rng-complete)
  (add-to-list 'nxml-completion-hook 't))

(use-package nxml-mode
  :defer t
  :init
  ;; treat <e><p>...</p></e> like (e (p ...)) for C-M-f/b/k/d/u
  (setq nxml-sexp-element-flag t)
  ;; complete the element after typing </
  (setq nxml-slash-auto-complete-flag t)
  :config
  ;; This is useful, should have a keybinding:
  (define-key nxml-mode-map (kbd "M-D") 'nxml-backward-down-element))

(use-package flycheck-apertium
  :ensure t
  :after dix)

(use-package flycheck
  :defer 10
  :ensure t
  :init
  (defun flycheck-mode-unless-big-dictionary ()
    "We want it in transfer files (which are dix-mode), just not dictionaries (which are big).'"
    (unless (and (buffer-file-name)
                 (string-match "\\.\\(meta\\|multi\\)?dix$" buffer-file-name))
      (flycheck-mode +1)))
  (add-hook 'dix-mode-hook #'flycheck-mode-unless-big-dictionary))

(use-package hfst-mode
  :defer t
  :ensure t
  :commands hfst-mode
  :mode (("\\.twol\\'" . hfst-mode)
         ("\\.twolc\\'" . hfst-mode)
         ("\\.xfst\\'" . hfst-mode)
         ("\\.lexc\\'" . hfst-mode)
         ("\\-lex.txt\\'" . hfst-mode)
         ("\\-lexc.txt\\'" . hfst-mode)
         ("\\-morph.txt\\'" . hfst-mode)
         ("\\.pmscript\\'" . hfst-mode)))

(add-to-list 'auto-mode-alist '("\\.relabel\\'" . whitespace-mode))

(use-package cg
  :load-path ("/opt/local/share/emacs/site-lisp"
              "/usr/local/share/emacs/site-lisp"
              "/usr/share/emacs/site-lisp")
  :defer t
  :mode (("\\.rlx\\'" . cg-mode)
         ("\\.lex\\'" . cg-mode)
         ("\\.rle\\'" . cg-mode)
         ("\\.cg\\'" . cg-mode)
         ("\\.cg3\\'" . cg-mode)
         ("\\.cg3r\\'" . cg-mode))
  :init
  (add-hook 'cg-mode-hook (lambda () (auto-fill-mode 0))))




(defconst apertium-c-style
  '((c-basic-offset . 2)
    (c-comment-only-line-offset . 0)
    (c-hanging-braces-alist
     (substatement-open before after))
    (c-offsets-alist
     (topmost-intro . 0)
     (substatement . +)
     (substatement-open . 0)
     (case-label . +)
     (access-label . -)
     (inclass . ++)
     (inline-open . 0)))
  "Apertium C++ Programming Style.")

(defun apertium-c-mode-common-hook ()
  "Add the Apertium C style and use it if we seem to be in an Apertium source file."
  (c-add-style "apertium" apertium-c-style t)
  (if (and (buffer-file-name)
           (string-match "/matxin/\\|/apertium/\\|/lttoolbox/"
                         (buffer-file-name)))
      (c-set-style "apertium")))

(use-package cc-mode
  :defer t
  :init
  (add-hook 'c-mode-common-hook #'apertium-c-mode-common-hook)
  ;; Make keys like M-f treat "camelCase" as two words:
  (add-hook 'c-mode-common-hook #'subword-mode))


(use-package which-key
  :ensure t
  :init (setq which-key-idle-delay 1.5)
  :config (which-key-mode))

(use-package company
  :defer t
  :ensure t
  :init
  (when (locate-library "company")
    (add-hook 'c++-mode-hook #'company-mode)
    (add-hook 'cg-mode-hook #'company-mode)
    (add-hook 'hfst-mode-hook #'company-mode))
  :config
  (setq company-minimum-prefix-length 2
        company-idle-delay 0.2
        company-show-numbers t)
  ;; It's annoying when you try to insert a newline and it inserts a completion:
  (unbind-key "RET" company-active-map)
  (unbind-key "<return>" company-active-map)
  ;; OTOH, users often expect TAB to do magical things; literal tabs
  ;; can always be inserted with C-q TAB
  (defun company-indent-or-complete ()
    "Indent if no prefix, otherwise complete."
    ;; from https://www.emacswiki.org/emacs/CompanyMode#toc10
    (interactive)
    (if (looking-at "\\_>")
        (company-complete-common)
      (indent-according-to-mode)))
  (define-key company-active-map [tab] 'company-indent-or-complete)
  (define-key company-active-map (kbd "TAB") 'company-indent-or-complete)
  ;; But if you've explicitly moved up/down the list of suggestions,
  (setq company-auto-complete #'company-explicit-action-p)
  ;; then whitespace/punctuation/close/comment-end(newline) will
  ;; complete (and self-insert):
  (setq company-auto-complete-chars '(?\  ?\) ?. ?>)))

(use-package company-statistics
  :ensure t
  :after company
  :config
  (setq company-statistics-size 800)
  (add-hook 'after-init-hook #'company-statistics-mode))

(use-package bind-key
  :config
  (bind-key* "C-+" #'text-scale-increase)
  (bind-key* "M-+" #'text-scale-decrease))


;; Back to sane value, less large freezes:
(setq gc-cons-threshold (* 4 1024 1024))

(provide 'init-apertium)
;;; init-apertium.el ends here
