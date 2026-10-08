#!/system/bin/sh
# Magisk installer script. APKs are added to the zip by build_module.sh.
SKIPUNZIP=0

[ "$API" -ge 31 ] || abort "! Android 12 (API 31) or newer required"

for apk in system/product/app/DeskClock/DeskClock.apk system/product/app/Glimpse/Glimpse.apk; do
  [ -f "$MODPATH/$apk" ] || abort "! Missing $apk in module zip"
done

set_perm_recursive "$MODPATH/system" 0 0 0755 0644
ui_print "- Stock Gallery and Clock removed, DeskClock and Glimpse installed as system apps"
