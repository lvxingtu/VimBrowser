### Ideas for things that might be implemented

Handling splits:
  1. When the split command is issued in a buffer, notify the parent window (tab) that a split has
     occured in that tab.
  2. The tab will then create the new window. It will shrink the size of the buffer that created the
     split in half based on which kind of split was created.
      * If it is a horizontal split, then the height of the calling buffer will be halved
      * If it is a vertical split, then the width of the calling buffer will be halved
  3. A buffer will keep track of the buffer that it split from
      * If the buffer it split from is deleted, it will fill the space of both of them

Keyboard Commands:
  1. Create a configuration file (potentially JSON) to handle the configuration of the brower's
     keyboard shortcuts
  2. Keyboard shortcuts for:
      1. Creating and destroying splits, both vertical and horizontal (ctrl-e and ctrl-shift-e)
      2. Openning and closing tabs (ctrl-t & ctrl-w)
      3. Going to the location bar of the current buffer (ctrl-l)
