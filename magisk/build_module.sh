#!/usr/bin/env bash
# Usage: build_module.sh <DeskClock.apk> <Glimpse.apk> [output.zip]
# Packs the two APKs into the Magisk module template.
set -euo pipefail
[ $# -ge 2 ] || { echo "usage: $0 DeskClock.apk Glimpse.apk [out.zip]"; exit 1; }
here="$(cd "$(dirname "$0")" && pwd)"
out="$(realpath -m "${3:-$here/los-apps-system.zip}")"
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
cp -r "$here/module/." "$tmp/"
mkdir -p "$tmp/system/product/app/DeskClock" "$tmp/system/product/app/Glimpse"
touch "$tmp/system/product/app/DeskClock/.replace"
cp "$1" "$tmp/system/product/app/DeskClock/DeskClock.apk"
cp "$2" "$tmp/system/product/app/Glimpse/Glimpse.apk"
mkdir -p "$tmp/system/etc/sysconfig" "$tmp/system/etc/default-permissions"
cp "$here/../com.android.deskclock_allowlist.xml" "$tmp/system/etc/sysconfig/com.android.deskclock_allowlist.xml"
cp "$here/../com.android.deskclock_default-permissions.xml" "$tmp/system/etc/default-permissions/default-permissions-com.android.deskclock.xml"
rm -f "$out"
(cd "$tmp" && zip -qr "$out" .)
echo "Built $out"
