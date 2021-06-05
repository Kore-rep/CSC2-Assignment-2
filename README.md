# CSC2-Assignment-2
---------------
This code is horribly coded and needs to be updated, but remains up for preservation
---------------
A water flow simulation created by Angus Longmore - LNGANG002

Reads in height data from data/ and converts it into a greyscale image.
Clicking on the image will add a block of water.
Click start to run the simulation.
More water can be added at any time by clicking anywhere on the image.

The Pause and Play buttons pause and unpause the simulation.
The Reset button will remove all the water and reset the step counter to 0.

To compile:

    > make

To run:

   For a medium sized grid:
   
    > make runMed 

   For a large sized grid:
   
    > make runLarge

To regenerate docs:

    > make docs

To clean all class files

    > make clean

