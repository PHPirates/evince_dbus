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

# This file offers forward search for evince.
import os
from traceback import print_exc

import dbus

line_number = 5
pdf_file = os.getcwd() + '/' + 'main.pdf'
tex_file = os.getcwd() + '/' + 'main.tex'

try:
    # Initialize a session bus
    bus = dbus.SessionBus()

    # Get a reference to the evince daemon object
    daemon = bus.get_object('org.gnome.evince.Daemon', '/org/gnome/evince/Daemon')

    # findDocument is a method provided by Evince on the dbus, see
    # https://mail.gnome.org/archives/commits-list/2010-July/msg02054.html
    # "It returns the name owner of the evince process for the given document
    #  URI."
    dbus_name: str = daemon.FindDocument('file://' + pdf_file, True, dbus_interface="org.gnome.evince.Daemon")
    print("evince process owner: " + dbus_name)  # Something like :1.149

    # Get the window remote object
    window = bus.get_object(dbus_name, '/org/gnome/evince/Window/0')

    # Call a method on the object: highlight a line in the pdf
    window.SyncView(tex_file, (line_number, 1), 0, dbus_interface="org.gnome.evince.Window")
except dbus.DBusException:
    print_exc()
