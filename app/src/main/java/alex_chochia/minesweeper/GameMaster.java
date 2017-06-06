//  GameMaster.java
//  Created by Alex Chochia on 2/24/2015.
//  Purpose: manages the logic of the game

package alex_chochia.minesweeper;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

class GameMaster implements View.OnClickListener, View.OnLongClickListener
{
    private Context parentContext;
    private BombButton bombArray[][];
    private int numOfAvailableFlags;
    private boolean bombsSet;
    private int numBombs;
    private int bombFlagged;
    private Random random;
    Toast message;

    GameMaster(Context context, int numBombs)
    {
        this.parentContext = context;
        this.numBombs = numBombs;
        this.numOfAvailableFlags = this.numBombs;
        this.bombArray = new BombButton[8][8];
        this.random = new Random();

        initialiseArray();
    }

    public void onClick(View view)
    {
        BombButton b = (BombButton) view;
        if (!bombsSet)
        {
            bombsSet = true;
            removeFlipping();
            setBombsToField(b);
        }
        else if (!b.isRevealed() && !b.isFlagged())
        {
            reveal(b.getXValue(), b.getYValue());
        }
    }

    public boolean onLongClick(View view)
    {
        BombButton b = (BombButton) view;

        if (numOfAvailableFlags == 0 && !b.isFlagged())
        {

            this.message.cancel();

            this.message = Toast.makeText(this.parentContext,
                    this.parentContext.getString(R.string.out_of_flags), Toast.LENGTH_SHORT);
            this.message.show();
        }
        else
        {
            if (!b.isFlagged())
            {
                b.setText(R.string.flag);
                b.setFlagged();
                if (b.isBomb())
                {
                    bombFlagged++;
                }

                numOfAvailableFlags--;
            } else
            {
                b.setFlagged();
                b.setText("");
                if (b.isBomb())
                {
                    bombFlagged--;
                }
                numOfAvailableFlags++;
            }

            if(message != null)
                this.message.cancel();

            this.message = Toast.makeText(this.parentContext,
                    ("You have " + numOfAvailableFlags + ((numOfAvailableFlags == 1) ?
                            " flag left" : " flags left")), Toast.LENGTH_SHORT);
            this.message.show();
        }

        if (this.bombFlagged == this.numBombs)
        {
            gameOver(true);
        }

        return true;
    }

    private void initialiseArray()
    {
        bombsSet = false;

        for (int i = 0; i < bombArray.length; i++)
        {
            for (int q = 0; q < bombArray[i].length; q++)
            {
                bombArray[i][q] = new BombButton(this.parentContext, i, q);
                bombArray[i][q].setId(i * 10 + q);
                bombArray[i][q].setOnClickListener(this);
            }
        }
    }

    int getArrayLength()
    {
        return bombArray.length;
    }

    BombButton getButton(int xValue, int yValue)
    {
        return bombArray[xValue][yValue];
    }

    private void setBombsToField(BombButton button)
    {
        this.bombFlagged = 0;

        for (int i = 0; i < numBombs; i++)
        {
            int x = (random.nextInt(bombArray.length));
            int y = (random.nextInt(bombArray.length));

            if (!bombArray[y][x].isBomb() && bombArray[y][x] != button)
            {
                bombArray[y][x].setBomb();
                for (int p = y - 1; p < y + 2; p++)
                {
                    for (int q = x - 1; q < x + 2; q++)
                    {
                        try
                        {
                            bombArray[p][q].incrementNeighbouringBombCount();
                        }

                        catch (ArrayIndexOutOfBoundsException e)
                        {
                            // TODO: nothing to do
                        }
                    }
                }
            }
            else
                i--;
        }

        for (BombButton[] bx: bombArray)
        {
            for (BombButton b: bx)
            {
                b.setOnLongClickListener(this);
            }
        }

        reveal(button.getXValue(), button.getYValue());
    }

    private void revealer(int x, int y)
    {
        bombArray[x][y].reveal();

        if (bombArray[x][y].getNeighbouringBombCount() == 0)
        {
            for (int r = x - 1; r < x + 2; r++)
                for (int c = y - 1; c < y + 2; c++)
                {
                    try
                    {
                        reveal(r, c);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // TODO: nothing to do
                    }
                }
        }
    }

    private void reveal(int x, int y)
    {
        if (!bombArray[x][y].isRevealed() && !bombArray[x][y].isFlagged())
        {
            revealer(x, y);
        }

        if (bombArray[x][y].isBomb())
        {
            gameOver(false);
        }
    }

    private void gameOver(boolean outcome)
    {
        if (outcome)
        {
            message = Toast.makeText(this.parentContext,
                    this.parentContext.getText(R.string.win), Toast.LENGTH_LONG);
            message.show();
        }

        else
        {
            message = Toast.makeText(this.parentContext,
                    this.parentContext.getText(R.string.lose), Toast.LENGTH_LONG);
            message.show();
        }

        endTheGame();
    }

    private void endTheGame()
    {
        for (BombButton[] bx: bombArray)
        {
            for (BombButton b: bx)
            {
                b.reveal();
            }
        }
    }

    void restart()
    {
        for (BombButton[] bx: bombArray)
        {
            for (BombButton b: bx)
            {
                if (b.isRevealed() || b.isFlagged())
                {
                    b.flip();
                }
                else
                {
                    b.clear();
                }
                b.setOnLongClickListener(null);
            }
        }

        this.numOfAvailableFlags = this.numBombs;
        this.bombFlagged = 0;
        this.bombsSet = false;
    }

    private void removeFlipping()
    {
        for (BombButton[] bx: bombArray)
        {
            for (BombButton b: bx)
            {
                b.removeFlipping();
            }
        }
    }
}
