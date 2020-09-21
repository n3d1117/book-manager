#!/bin/bash
# Source: https://joel-costigliola.github.io/assertj/assertj-swing-running.html

# Configure VNC password
umask 0077
mkdir -p "$HOME/.vnc"
chmod go-rwx "$HOME/.vnc"
vncpasswd -f <<<"7y%r&jwk" >"$HOME/.vnc/passwd"

NEW_DISPLAY=42
DONE="no"

while [ "$DONE" == "no" ]
do
  out=$(xdpyinfo -display :${NEW_DISPLAY} 2>&1)
  if [[ "$out" == name* ]] || [[ "$out" == Invalid* ]]
  then
    # command succeeded; or failed with access error;  display exists
    (( NEW_DISPLAY+=1 ))
  else
    # display doesn't exist
    DONE="yes"
  fi
done

echo "Using first available display :${NEW_DISPLAY}"

OLD_DISPLAY=${DISPLAY}
vncserver ":${NEW_DISPLAY}" -localhost -geometry 1600x1200 -depth 16
export DISPLAY=:${NEW_DISPLAY}

"$@"
EXIT_CODE=$?

export DISPLAY=${OLD_DISPLAY}
vncserver -kill ":${NEW_DISPLAY}"

exit $EXIT_CODE
