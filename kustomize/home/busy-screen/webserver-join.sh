#!/bin/bash
'timeout' '300s' 'sh' '-c' 'while true; do
  _block=▀
  _reset="$(tput sgr0)"
  _frame=${_frame:-0}
  _frames=2
  _frameCols=89
  _frameRows=7
  upper_gray="$(tput setaf 0)"
  lower_gray="$(tput setab 0)"
  upper_white="$(tput setaf 3)"
  lower_white="$(tput setab 3)"
  upper_red="$(tput setaf 1)"
  lower_red="$(tput setab 1)"
  upper_black="$(tput setaf 7)"
  lower_black="$(tput setab 7)"
  if [ $_frame -eq 0 ]; then
    upper_white="$upper_gray"
    lower_white="$lower_gray"
    upper_red="$upper_gray"
    lower_red="$lower_gray"
  fi
  ww="$lower_white$upper_white${_block}$_reset"
  rw="$lower_white$upper_red${_block}$_reset"
  xw="$lower_white$upper_black${_block}$_reset"
  tw="$lower_white$upper_gray${_block}$_reset"
  wr="$lower_red$upper_white${_block}$_reset"
  rr="$lower_red$upper_red${_block}$_reset"
  xr="$lower_red$upper_black${_block}$_reset"
  tr="$lower_red$upper_gray${_block}$_reset"
  wx="$lower_black$upper_white${_block}$_reset"
  rx="$lower_black$upper_red${_block}$_reset"
  xx="$lower_black$upper_black${_block}$_reset"
  tx="$lower_black$upper_gray${_block}$_reset"
  wt="$lower_gray$upper_white${_block}$_reset"
  rt="$lower_gray$upper_red${_block}$_reset"
  xt="$lower_gray$upper_black${_block}$_reset"
  tt="$lower_gray$upper_gray${_block}$_reset"
  cols=$(tput cols)
  rows=$(tput lines)
  top_rows=$(((rows-_frameRows)/2))
  bottom_rows=$((rows-_frameRows-top_rows-1))
  line=$lower_gray$(printf "%*.0s\n" "$cols" "" | tr " " " ")$_reset
  if [ "$cols" -lt "$_frameCols" ]; then
    prefix_cols=$(((cols-15)/2))
    postfix_cols=$((cols-15-prefix_cols))
    prefix=$lower_gray$(printf "%*.0s" "$prefix_cols" "" | tr " " " ")$_reset
    postfix=$lower_gray$(printf "%*.0s" "$postfix_cols" "" | tr " " " ")$_reset
    tput home
    for x in $(seq 1 "$top_rows"); do echo "$line"; done
    echo "$prefix$tt$tx$xr$xr$xr$tx$tt$tt$tt$tx$xt$xt$xt$tx$tt$postfix"
    echo "$prefix$xx$rr$ww$wr$rr$rr$xr$tx$xt$tt$tt$tt$tt$tt$xx$postfix"
    echo "$prefix$xx$rr$rr$rr$rr$rr$rr$rr$tt$tt$tt$tt$tt$tt$xx$postfix"
    echo "$prefix$tt$xt$rx$rr$rr$rr$rr$rr$tt$tt$tt$tt$tx$xt$tt$postfix"
    echo "$prefix$tt$tt$tt$xt$rx$rr$rr$rr$tt$tt$tx$xt$tt$tt$tt$postfix"
    echo "$prefix$tt$tt$tt$tt$tt$xt$rx$rr$tx$xt$tt$tt$tt$tt$tt$postfix"
    echo "$prefix$tt$tt$tt$tt$tt$tt$tt$xt$tt$tt$tt$tt$tt$tt$tt$postfix"
    for x in $(seq 1 "$bottom_rows"); do echo "$line"; done
    printf "$line"
  else
    prefix_cols=$(((cols-_frameCols)/2))
    postfix_cols=$((cols-_frameCols-prefix_cols))
    prefix=$lower_gray$(printf "%*.0s" "$prefix_cols" "" | tr " " " ")$_reset
    postfix=$lower_gray$(printf "%*.0s" "$postfix_cols" "" | tr " " " ")$_reset
    tput home
    for x in $(seq 1 "$top_rows"); do echo "$line"; done
    echo "$prefix$tt$tx$xr$xr$xr$tx$tt$tt$tt$tx$xt$xt$xt$tx$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$postfix"
    echo "$prefix$xx$rr$ww$wr$rr$rr$xr$tx$xt$tt$tt$tt$tt$tt$xx$tt$tt$tt$tt$tt$tx$tx$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tx$tx$tt$tt$tt$tx$tx$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$postfix"
    echo "$prefix$xx$rr$rr$rr$rr$rr$rr$rr$tt$tt$tt$tt$tt$tt$xx$tt$tt$tt$tt$tt$xx$xx$tt$tt$tt$tt$tt$tt$tx$tx$tx$tx$tx$tt$tt$tt$tx$tx$tx$tx$tx$tt$tt$tt$tx$tx$tx$tx$xx$xx$tt$tt$tx$tx$tx$tt$tt$tt$tx$tx$tx$tx$tx$tx$tt$tt$tt$tx$tx$tx$tx$tx$tx$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$postfix"
    echo "$prefix$tt$xt$rx$rr$rr$rr$rr$rr$tt$tt$tt$tt$tx$xt$tt$tt$tt$tt$tt$tt$xx$xx$tt$tt$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tx$tx$tx$tx$xx$xx$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$postfix"
    echo "$prefix$tt$tt$tt$xt$rx$rr$rr$rr$tt$tt$tx$xt$tt$tt$tt$tt$tt$tt$tt$tt$xx$xx$tt$tt$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$xx$xx$tt$tt$tt$xx$xx$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$tt$tt$xx$xx$tt$xt$xx$tx$tx$tx$xx$xx$tt$tt$tt$tt$tx$tx$tt$tt$tt$tx$tx$tt$tt$tt$tx$tx$postfix"
    echo "$prefix$tt$tt$tt$tt$tt$xt$rx$rr$tx$xt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$xt$xt$xt$xt$xt$xt$tt$tt$xt$xt$xt$xt$xt$tt$tt$tt$xt$xt$xt$xt$xt$xt$tt$tt$xt$xt$xt$xt$xt$xt$tt$xt$xt$xt$xt$xt$xt$tt$xt$xt$tt$tt$tt$xt$xt$tt$tt$tt$tt$tt$tt$xx$xx$tt$tt$tt$tt$xt$xt$tt$tt$tt$xt$xt$tt$tt$tt$xt$xt$postfix"
    echo "$prefix$tt$tt$tt$tt$tt$tt$tt$xt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$xt$xt$xt$xt$xt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$tt$postfix"
    for x in $(seq 1 "$bottom_rows"); do echo "$line"; done
    printf "$line"
  fi
  _frame=$((_frame+1))
  if [ $_frame -ge $_frames ]; then _frame=0; fi
  curl --silent --fail --max-time 1 '"'"'http://localhost/'"'"' > /dev/null \
    && curl --silent --fail --max-time 1 '"'"'http://localhost:1880/'"'"' > /dev/null \
    && break
  sleep 1
done'
