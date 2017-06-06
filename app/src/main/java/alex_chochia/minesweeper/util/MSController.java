//  Created by Alex on 2/24/2015.
//  MS CONTROLLER

package alex_chochia.minesweeper.util;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Random;
import alex_chochia.minesweeper.BombButton;
import alex_chochia.minesweeper.R;

public class MSController implements View.OnClickListener, View.OnLongClickListener
{
    private BombButton bombArray[][];
    private boolean bombsSet;
    private int numBombs;
    private int flaggedItems;
    private ProgressBar flaggedStatus;
    private Context context;

    public MSController(Context context, int numBombs)
    {
        this.numBombs = numBombs;
        this.bombArray = new BombButton[numBombs][numBombs];
        this.context = context;
        initialiseArray();
        this.bombsSet = false;
    }

    public void onClick(View view)
    {
        BombButton b = (BombButton) view;
        if (!bombsSet)
        {
            bombsSet = true;
            setBombsToField(b);
        }
        else
        {
            reveal(b.getXValue(), b.getYValue());
        }
    }

    public boolean onLongClick(View view)
    {
        BombButton b = (BombButton) view;
        if (!b.isFlagged())
        {
            b.setOnClickListener(null);
            b.setFlagged();
            flaggedItems++;
        }

        else
        {
            b.setOnClickListener(this);
            b.setBackgroundResource(android.R.drawable.btn_default);
            b.setFlagged();
            flaggedItems--;
        }

        flaggedStatus.setProgress(flaggedItems);
        isItOver();
        return true;
    }

    private void initialiseArray()
    {
        flaggedStatus = new ProgressBar(this.context, null, R.attr.progressBarStyle);
        flaggedStatus.setMax(numBombs);

        for (int i = 0; i < bombArray.length; i++)
            for (int q = 0; q < bombArray[i].length; q++)
            {
                bombArray[i][q] = new BombButton(this.context, i, q);
                bombArray[i][q].setId(i*10 + q);
                bombArray[i][q].setOnClickListener(this);
                bombArray[i][q].setOnLongClickListener(this);
            }
    }

//    public int getArrayLength()
//    {
//        return bombArray.length;
//    }
//
//    public ProgressBar getFlaggedStatus()
//    {
//        return this.flaggedStatus;
//    }
//
//    public BombButton getButton(int xValue, int yValue)
//    {
//        return bombArray[xValue][yValue];
//    }

    private void setBombsToField(BombButton button)
    {
        for (int i = 0; i < numBombs; i++)
        {
            int x = (new Random().nextInt(bombArray.length));
            int y = (new Random().nextInt(bombArray.length));

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
                            // do nothing
                        }
                    }
                }
            }
            else
                i--;
        }

        reveal(button.getXValue(), button.getYValue());
    }

    private void revealer(int x, int y)
    {
        bombArray[x][y].reveal();

        for (int r = x - 1; r < x + 2; r++)
            for (int c = y - 1; c < y + 2; c++) {
                try {
                    reveal(r, c);
                } catch (Exception e) {
                    // TODO: nothing to do
                }
            }
    }

    private void reveal(int x, int y)
    {
        if (!bombArray[x][y].isRevealed() && !bombArray[x][y].isFlagged())
        {
            if (bombArray[x][y].getNeighbouringBombCount() == 0)
                revealer(x, y);
            else
                bombArray[x][y].reveal(); // unveils the content of the button
        }
        isItOver();
    }

    private void isItOver()
    {
        int flagged = 0;
        for (int i = 0; i < bombArray.length; i++)
            for (int q = 0; q < bombArray[i].length; q++)
            {
                if (bombArray[i][q].isFlagged() && bombArray[i][q].isBomb())
                {
                    if (++flagged == numBombs)
                        gameOver(true);
                }
                else if (bombArray[i][q].isRevealed() && bombArray[i][q].isBomb())
                    gameOver(false);
            }
    }

    private void gameOver(boolean outcome)
    {
        if (outcome)
        {
            Toast win = Toast.makeText(context, "You Win!!!", Toast.LENGTH_LONG);
            win.show();

        }
        else
        {
            Toast lose = Toast.makeText(context, "You lost...", Toast.LENGTH_LONG);
            lose.show();
        }
    }

    public int getFlaggedItems()
    {
        return this.flaggedItems;
    }

    public int getNumBombs()
    {
        return this.numBombs;
    }
}
