What is in this repo?
=====================

This repo contains some ready-to-run files which are minimal examples of how to communicate with Evince using D-Bus in order to forward/backward search between ``.tex`` and ``.pdf`` files.
They exist in Python, Java and Kotlin, in the corresponding folders.

The Python code is based on an existing `Synctex plugin <https://github.com/GNOME/gedit-plugins/blob/master/plugins/synctex/synctex/evince_dbus.py>`_ for Gedit.
The Java code is based on the Python code and the (very sparse) `documentation <https://dbus.freedesktop.org/doc/dbus-java/dbus-java>`_ for the Java D-Bus bindings.
The Kotlin code is essentially a working summary of the Java code, without very lengthy explanations in the code.


Python
======


This repo contains some ready-to-run Python files which are minimal examples of how to communicate with Evince using D-Bus in order to forward/backward search between ``.tex`` and ``.pdf`` files.

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

In this case it was not documented which methods were exported by Evince to the D-Bus.
Fortunately, it is possible to inspect what methods are available using the Gnome d-feet program or actually connecting with D-Bus directly with some code.
First d-feet.
On Ubuntu you can install it with ``sudo apt-get install d-feet``, on Arch Linux I did the following.

* On the homepage at https://wiki.gnome.org/action/show/Apps/DFeet there is a link to the latest stable sources, at the moment of writing it is http://download.gnome.org/sources/d-feet/0.3/d-feet-0.3.14.tar.xz
* Download and extract
* Install the packages ``itstool intltool gobject-introspection``
* Run ``./configure`` and ``make`` and ``sudo make install``
* Run ``d-feet``

Now under Session Bus, type evince and you'll see under the  ``/org/gnome/evince/Daemon`` object path (as you see this is used in the code) and ``org.gnome.evince.Daemon`` interface (also used in the code) the methods available.

As for the code (Java, in this case), see https://dbus.freedesktop.org/doc/dbus-java/dbus-java/dbus-javase2.html#x9-130002 and also the forward search example in this repo.

Working with D-Bus
------------------

If you get a DBusExecutionException, you can see in the stacktrace that it's probably thrown in ``AbstractConnection.java``, if you break there you'll see the actual exception which is caught there.
For example in my case I saw a NoSuchMethodException because it was searching in the wrong Interface (org.freedesktop.dbus.interfaces.Introspectable instead of org.gnome.evince.Daemon).

Executing methods exported on the D-Bus
--------------------------------------

So suppose you have found a method exported on the D-Bus, for example ``FindDocument`` under the interface name ``org.gnome.evince.Deamon``.
Then in order to call it, you should create a Java interface called ``Daemon`` in the package ``org.gnome.evince``.
In the interface you should declare the methods you want to use, with the right signature.
Then you can get the object with ``Daemon interfaceDaemon = connection.getRemoteObject("org.gnome.evince.Daemon", "/org/gnome/evince/Deamon", Daemon.class);`` and you can call the method with ``String owner = interfaceDaemon.FindDocument(pdfFile, true);``.


Now the remaining challenge is to find the interface name and method signatures of other methods you want to call but which you can't find in d-feet.
I just searched in the source of Evince and found `D-Bus API <https://dbus.freedesktop.org/doc/dbus-api-design.html>`_ XML files, for example https://github.com/GNOME/evince/blob/master/shell/ev-gdbus.xml when searching for SyncView. This is the same xml data which you can get using introspection (as mentioned before). Example:

.. code-block:: xml

    <interface name='org.gnome.evince.Window'>
        <method name='SyncView'>
          <arg type='s' name='source_file' direction='in'/>
          <arg type='(ii)' name='source_point' direction='in'/>
          <arg type='u' name='timestamp' direction='in'/>
        </method>
    </interface>

In order to find out what these argument types are you can look in the D-Bus specification at https://dbus.freedesktop.org/doc/dbus-specification.html#type-system.
Regarding Java, in order to find out how to represent types in Java the dbus-java documentation at https://dbus.freedesktop.org/doc/dbus-java/dbus-java/dbus-javase7.html may help.

In the example, 's' is a String, 'u' an unsigned 32-bit integer which can be represented in Java by a dbus-java type UInt32, and '(ii)' is a block of values containing two ints (in D-Bus they are signed two's complement 32-bit integers). The brackets indicated they appear together in a struct, which can be represented in Python with a tuple (apparently) and in Java with something that extends Struct.

Executing methods via the terminal
----------------------------------

You can also execute methods directly via a terminal.
In this example, when you know the evince process owner (e.g. ``:1.195``) which you get by executing FindDocument (see Python or Java examples) then you can execute SyncView by running in the location of your tex file (where 11 is the line number)::

    gdbus call --session --dest :1.195 --object-path /org/gnome/evince/Window/0 --method org.gnome.evince.Window.SyncView "main.tex" "(11, 1)" "0"

In this case ``:1.195`` is the object name, like ``org.gnome.evince.Daemon`` was for FindDocument. You can view all names by executing ``qdbus`` and available object paths with ``qdbus :1.195`` where ``:1.195`` is an object name.
You can also view available methods with ``qdbus :1.195 /org/gnome/evince/Window/0``.

So theoretically,::

    gdbus call --session --dest org.gnome.evince.Daemon --object-path /org/gnome/evince/Daemon --method org.gnome.evince.FindDocument "main.pdf" "true"

should work to find the process owner name, but this results in a DBus.Error.UnknownMethod

Debugging signals
-----------------

You can view all signals going over the DBus by executing ``dbus-monitor --session``, also exceptions will be shown and the contents of the signal (which you could also view by placing a breakpoint at the point in the code where the DBusException is thrown).