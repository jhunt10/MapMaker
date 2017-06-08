# MapMaker

This is a simple map making tool for tile based games. The tile set must be 16 x 8
in groups of 16 with 20 x 20 tiles, saved as a png. If that doesn't make sense look 
at the exmple tile sets provided in the input folder.

Right and left click on the tiles in the tool window to pick which tiles to draw with.
Any tiles in Base or Pass 1-3 will allow the player to walk though them, Ass and Block 1-3 will not. 
Fill can stall for a bit on very large areas so be patient. 
Since the player always stays centered in the screen they can not get 
with in 9 x or 16 y of the edge, the mouse over x and y will turn red to let you know.
Tiles with black in the backgound are transparent and can be stacked ontop of others.
Once saved the map is output as a png image file with a txt file of the tile codes.
