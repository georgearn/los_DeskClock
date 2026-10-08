#!/system/bin/sh
# Magisk installer script. APKs are shipped inside the zip under system/.
SKIPUNZIP=0

[ "$API" -ge 31 ] || abort "! Android 12 (API 31) or newer required"

for apk in system/app/DeskClock/DeskClock.apk system/app/Glimpse/Glimpse.apk; do
  [ -f "$MODPATH/$apk" ] || abort "! Missing $apk in module zip"
done

set_perm_recursive "$MODPATH/system" 0 0 0755 0644
ui_print "- DeskClock and Glimpse will be installed as system apps"
ui_print "- Uninstall the user-installed copies first if the apps do not appear"
