""" This file is a minimal example for using backward search with Evince via dbus. """
import os
import re
import subprocess
import urllib.parse

import dbus.mainloop.glib
# Requires PyGObject to be installed (Python bindings to GObject based libraries such as GLib)
from gi.repository import GLib

pdf_file = os.getcwd() + '/' + 'main.pdf'
tex_file = os.getcwd() + '/' + 'main.tex'

# Command to execute for backward search, where %f is the tex file to load and %l is the line number
command = "~/.local/share/JetBrains/Toolbox/apps/PyCharm-P/ch-0/183.5153.39/bin/pycharm.sh --line %l %f"

dbus_name = ''


def on_sync_source(input_file, source_link, timestamp):
    """ Open the editor at the right place. """
    print(input_file + ":" + str(source_link[0]))
    cmd = re.sub("%f", '"' + urllib.parse.unquote(input_file.split("file://")[1]) + '"', command)
    cmd = re.sub("%l", str(source_link[0]), cmd)
    print(cmd)
    subprocess.call(cmd, shell=True)


def register_syncsource_by_process_owner(evince_name):
    """ Given the process owner of a document, register the SyncSource signal handler which handles the backward search call.  """
    # Get the remote object using object name and path
    window_obj = bus.get_object(dbus_name, '/org/gnome/evince/Window/0')
    window = dbus.Interface(window_obj, "org.gnome.evince.Window")
    window.connect_to_signal("SyncSource", on_sync_source)


def handle_find_document_error(error):
    """ Has to be declared when a reply function is used. """
    print("Handling error, FindDocument DBus call has failed:")
    print(error)


def register_syncsource_after_document_load(uri, **keyargs):
    """ Called when the document is opened in Evince. Will register the SyncSource signal handler, for the actual backward search. """
    # Get process owner
    process_owner = keyargs['sender']

    # Register SyncSource signal handler
    register_syncsource_by_process_owner(process_owner)


try:
    # This has to happen before initializing the dbus
    dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

    # Initialize the session bus
    bus = dbus.SessionBus()

    # Get the Daemon object
    daemon = bus.get_object('org.gnome.evince.Daemon', '/org/gnome/evince/Daemon')

    # Specify which method to call when the document is loaded
    # on_doc_loaded is the handler function called when the signal org.gnome.evince.Window.DocumentLoaded is received
    # The sender_keyword is the argument name by which the source of the signal will be given to the handler. See:
    # https://dbus.freedesktop.org/doc/dbus-python/tutorial.html#getting-more-information-from-a-signal
    # The following line will register the SyncSource signal
    # todo this is unnecessary as it does the same as the next FindDocument line, both calling register_syncsource_by_process_owner
    # bus.add_signal_receiver(register_syncsource_after_document_load, signal_name="DocumentLoaded",
    #                         dbus_interface="org.gnome.evince.Window",
    #                         sender_keyword='sender')

    # Specify which method is to be used to find the document
    # When the document is loaded, register the SyncSource signal
    dbus_name = daemon.FindDocument('file://' + pdf_file, True,
                                    reply_handler=register_syncsource_by_process_owner,
                                    error_handler=handle_find_document_error,
                                    dbus_interface="org.gnome.evince.Daemon")

    loop = GLib.MainLoop()
    loop.run()

except dbus.DBusException:
    print("Could not connect to the Evince Daemon.")
