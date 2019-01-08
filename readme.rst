This repo contains some ready-to-run Python files which are examples of how to communicate with Evince using dbus in order to forward/backward search between ``.tex`` and ``.pdf`` files.

Requirements
------------

* Install PyGObject using instructions from https://pygobject.readthedocs.io/en/latest/getting_started.html (tested on Arch Linux)

Backward search example
-----------------------
* For a backward search example, change the path to your PyCharm executable in ``evince_backward_search.py`` and run the file.
* While that is running, open ``main.pdf``.
* Hold <kbd>Ctrl</kbd> and click in the document, then PyCharm should open ``main.tex`` and focus on the right line.

Forward search example
----------------------
* Execute ``evince_dbus.py`` with Python, the line number (in the ``.tex`` file) and filenames are hard-coded in the main function.
* Evince should open and highlight the right line in the pdf file.
* At the moment, when you re-execute when Evince is still open an error will be thrown, but it still works.
* ``evince_forward_search_minimal`` is a much more minimal file which executes forward search exactly once and then quits.

Sources
-------

The file `evince_dbus.py <evince_dbus.py>`_ is a fork from the gedit synctex plugin file at https://github.com/GNOME/gedit-plugins/blob/master/plugins/synctex/synctex/evince_dbus.py.

The file `evince_backward_search.py <evince_backward_search.py>`_ is a fork from the file at https://github.com/gauteh/vim-evince-synctex/blob/master/bin/evince_backward_search

The file `evince_forward_search_minimal.py <evince_forward_search_minimal.py>`_ is a fork from the file at File from https://github.com/ebranlard/evince-backward-forward-search/blob/master/evince_forward_search