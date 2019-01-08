#!/usr/bin/python

# File from https://github.com/ebranlard/evince-backward-forward-search/blob/master/evince_forward_search

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

import dbus, subprocess, time, sys, os

def print_usage():
    print 'Usage: evince_forward_search pdf_file line_number tex_file'
    sys.exit(1)

if len(sys.argv)!=4:
    print_usage()
try:
    line_number = int(sys.argv[2])
except ValueError:
    print_usage()

pdf_file = os.path.abspath(sys.argv[1])
tex_file = os.path.abspath(sys.argv[3])

try:
    bus = dbus.SessionBus()
    daemon = bus.get_object('org.gnome.evince.Daemon', '/org/gnome/evince/Daemon')
    dbus_name = daemon.FindDocument('file://' + pdf_file, True, dbus_interface = "org.gnome.evince.Daemon")
    window = bus.get_object(dbus_name, '/org/gnome/evince/Window/0')
except dbus.DBusException:
    print_exc()

window.SyncView(tex_file, (line_number,1), 0, dbus_interface="org.gnome.evince.Window")