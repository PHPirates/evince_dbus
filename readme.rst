Python
======


This repo contains some ready-to-run Python files which are minimal examples of how to communicate with Evince using dbus in order to forward/backward search between ``.tex`` and ``.pdf`` files.

Requirements
------------

* Install PyGObject using instructions from https://pygobject.readthedocs.io/en/latest/getting_started.html (tested on Arch Linux)

Backward search minimal example
-------------------------------

* For a backward search example, change the path to your PyCharm executable in ``evince_backward_search_minimal.py`` and run the file.
* While that is running, open ``main.pdf`` if is not already opened.
* Hold Ctrl and click in the document, then PyCharm should open ``main.tex`` and focus on the right line.
* For the original (longer) example, see ``evince_backward_search.py``.

Forward search minimal example
------------------------------

* Execute ``evince_forward_search_minimal.py`` with Python, the line number (in the ``.tex`` file) and filenames are hard-coded.
* Evince should open and highlight the right line in the pdf file.
* At the moment, when you re-execute when Evince is still open an error will be thrown, but it still works.
* For the original (longer) example, see ``evince_dbus.py`` which also stays running instead of quitting after executing forward search once.

Sources
-------

The file `evince_dbus.py <evince_dbus.py>`_ is a fork from the gedit synctex plugin file at https://github.com/GNOME/gedit-plugins/blob/master/plugins/synctex/synctex/evince_dbus.py.

The file `evince_backward_search.py <evince_backward_search.py>`_ is a fork from the file at https://github.com/gauteh/vim-evince-synctex/blob/master/bin/evince_backward_search

The file `evince_forward_search_minimal.py <evince_forward_search_minimal.py>`_ is a fork from the file at https://github.com/ebranlard/evince-backward-forward-search/blob/master/evince_forward_search


DBus Documentation: d-feet
==========================

In this case it was not documented which methods were exported by Evince to the dbus.
Fortunately, it is possible to inspect what methods are available using the Gnome d-feet program or actually connecting with dbus directly with some code.
First d-feet.
On Ubuntu you can install it with ``sudo apt-get install d-feet``, on Arch Linux I did the following.

* On the homepage at https://wiki.gnome.org/action/show/Apps/DFeet there is a link to the latest stable sources, at the moment of writing it is http://download.gnome.org/sources/d-feet/0.3/d-feet-0.3.14.tar.xz
* Download and extract
* Install the packages ``itstool intltool gobject-introspection``
* Run ``./configure`` and ``make`` and ``sudo make install``
* Run ``d-feet``

Now under Session Bus, type evince and you'll see under the  ``/org/gnome/evince/Daemon`` object path (as you see this is used in the code) and ``org.gnome.evince.Daemon`` interface (also used in the code) the methods available.

As for the code (Java, in this case), see https://dbus.freedesktop.org/doc/dbus-java/dbus-java/dbus-javase2.html#x9-130002 and also the forward search example in this repo.

Working with dbus
------------------

If you get a DBusExecutionException, you can see in the stacktrace that it's probably thrown in ``AbstractConnection.java``, if you break there you'll see the actual exception which is caught there.
For example in my case I saw a NoSuchMethodException because it was searching in the wrong Interface (org.freedesktop.dbus.interfaces.Introspectable instead of org.gnome.evince.Daemon).