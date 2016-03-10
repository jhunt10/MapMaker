# MapMaker
Whole thing is pretty buggy and no effort has been made for error handeling. 
Right and left click on the tiles in the tool window to pick which tiles to draw with
Fill is really prone to stack overflow errors so use sparingly
Since the player always stays centered in the screen they can not get 
  with in 9 x or 16 y of the edge, the mouse over x and y will turn red to let you know.
Any tiles in Base or Pass 1-3 will allow the player to walk though them, Ass and Block 1-3 will not. 
Tiles with black in the backgound are transparent and can be stacked ontop of others.
Row and Colum 0 are being set to null and I'm not sure why yet.
