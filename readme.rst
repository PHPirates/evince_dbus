The file `evince_dbus.py <evince_dbus.py>`_ is a fork from the gedit synctex plugin file at https://github.com/GNOME/gedit-plugins/blob/master/plugins/synctex/synctex/evince_dbus.py.

It aims to be an example of how to communicate with Evince using dbus in order to forward/backward search between ``.tex`` and ``.pdf`` files.

Requirements
------------

* Install PyGObject using instructions from https://pygobject.readthedocs.io/en/latest/getting_started.html (tested on Arch Linux)

Backward search example
-----------------------
* For a backward search example, execute in a terminal
``./evince_backward_search main.pdf "~/.local/share/JetBrains/Toolbox/apps/PyCharm-P/ch-0/183.4886.43/bin/pycharm.sh --line %l %f"`` of course changing the path to your PyCharm.
* While that is running, open ``main.pdf``.
* Hold <kbd>Ctrl</kbd> and click in the document, then PyCharm should open ``main.tex`` and focus on the right line.

Forward search example
----------------------
* Execute ``evince_dbus.py`` with Python, the line number (in the ``.tex`` file) and filenames are hard-coded in the main function.
* Evince should open and highlight the right line in the pdf file.

<!-- todo test minimal example -->


Notes
-----

PyCharm inverse search:
``~/.local/share/JetBrains/Toolbox/apps/PyCharm-P/ch-0/183.4886.43/bin/pycharm.sh --line 10 ~/GitRepos/evince_dbus/main.tex``