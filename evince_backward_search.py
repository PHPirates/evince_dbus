#!/usr/bin/python
# -*- coding: utf-8 -*-

# Copyright (C) 2010 Jose Aliste <jose.aliste@gmail.com>
#               2011 Benjamin Kellermann <Benjamin.Kellermann@tu-dresden.de>
#
# This program is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public Licence as published by the Free Software
# Foundation; either version 2 of the Licence, or (at your option) any later
# version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE.  See the GNU General Public Licence for more
# details.
#
# You should have received a copy of the GNU General Public Licence along with
# this program; if not, write to the Free Software Foundation, Inc., 51 Franklin
# Street, Fifth Floor, Boston, MA  02110-1301, USA

import re
import subprocess
import urllib
import urllib.parse

import dbus

RUNNING, CLOSED = range(2)

EV_DAEMON_PATH = "/org/gnome/evince/Daemon"
EV_DAEMON_NAME = "org.gnome.evince.Daemon"
EV_DAEMON_IFACE = "org.gnome.evince.Daemon"

EVINCE_PATH = "/org/gnome/evince/Evince"
EVINCE_IFACE = "org.gnome.evince.Application"

EV_WINDOW_IFACE = "org.gnome.evince.Window"


class EvinceWindowProxy:
    """A DBUS proxy for an Evince Window."""
    daemon = None
    bus = None

    def __init__(self, uri, editor, spawn=False, logger=None):
        self._log = logger
        self.uri = uri
        self.editor = editor
        self.status = CLOSED
        self.source_handler = None
        self.dbus_name = ''
        self._handler = None
        try:
            if EvinceWindowProxy.bus is None:
                EvinceWindowProxy.bus = dbus.SessionBus()

            if EvinceWindowProxy.daemon is None:
                EvinceWindowProxy.daemon = EvinceWindowProxy.bus.get_object(EV_DAEMON_NAME,
                                                                            EV_DAEMON_PATH,
                                                                            follow_name_owner_changes=True)
            EvinceWindowProxy.bus.add_signal_receiver(self._on_doc_loaded, signal_name="DocumentLoaded",
                                                      dbus_interface=EV_WINDOW_IFACE,
                                                      sender_keyword='sender')
            self._get_dbus_name(False)

        except dbus.DBusException:
            if self._log:
                self._log.debug("Could not connect to the Evince Daemon")

    def _on_doc_loaded(self, uri, **keyargs):
        if uri == self.uri and self._handler is None:
            self.handle_find_document_reply(keyargs['sender'])

    def _get_dbus_name(self, spawn):
        EvinceWindowProxy.daemon.FindDocument(self.uri, spawn,
                                              reply_handler=self.handle_find_document_reply,
                                              error_handler=self.handle_find_document_error,
                                              dbus_interface=EV_DAEMON_IFACE)

    def handle_find_document_error(self, error):
        if self._log:
            self._log.debug("FindDocument DBus call has failed")

    def handle_find_document_reply(self, evince_name):
        if self._handler is not None:
            handler = self._handler
        else:
            handler = self.handle_get_window_list_reply
        if evince_name != '':
            self.dbus_name = evince_name
            self.status = RUNNING
            self.evince = EvinceWindowProxy.bus.get_object(self.dbus_name, EVINCE_PATH)
            self.evince.GetWindowList(dbus_interface=EVINCE_IFACE,
                                      reply_handler=handler,
                                      error_handler=self.handle_get_window_list_error)

    def handle_get_window_list_error(self, e):
        if self._log:
            self._log.debug("GetWindowList DBus call has failed")

    def handle_get_window_list_reply(self, window_list):
        if len(window_list) > 0:
            window_obj = EvinceWindowProxy.bus.get_object(self.dbus_name, window_list[0])
            self.window = dbus.Interface(window_obj, EV_WINDOW_IFACE)
            self.window.connect_to_signal("SyncSource", self.on_sync_source)
        else:
            # That should never happen.
            if self._log:
                self._log.debug("GetWindowList returned empty list")

    def on_sync_source(self, input_file, source_link, timestamp):
        print(input_file + ":" + str(source_link[0]))
        cmd = re.sub("%f", '"' + urllib.parse.unquote(input_file.split("file://")[1]) + '"', self.editor)
        cmd = re.sub("%l", str(source_link[0]), cmd)
        print(cmd)
        subprocess.call(cmd, shell=True)
        if self.source_handler is not None:
            self.source_handler(input_file, source_link)


# This file offers backward search in any editor.
if __name__ == '__main__':
    import dbus.mainloop.glib
    from gi.repository import GLib
    import os

    # pdf file to watch
    pdf_file = os.path.abspath('main.pdf')

    # Command to execute for backward search, where %f is the tex file to load and %l is the line number
    command = "~/.local/share/JetBrains/Toolbox/apps/PyCharm-P/ch-0/183.4886.43/bin/pycharm.sh --line %l %f"

    dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
    a = EvinceWindowProxy('file://' + urllib.parse.quote(pdf_file, safe="%/:=&?~#+!$,;'@()*[]"), command, True)

    loop = GLib.MainLoop()
    loop.run()
    # ex:ts=4:et:
