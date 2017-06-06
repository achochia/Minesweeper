// BombButton.java
// Created by Alex Chochia on 2/23/2015.
// Purpose: extends the Button class in order to add the features needed in the game

package alex_chochia.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class BombButton extends Button implements Animation.AnimationListener
{
    // MARK - instance variables
    private int xValue, yValue, neighbouringBombCount;
    private boolean isBomb, isRevealed, isFlagged, isFlipping;
    protected Animation toMiddle, fromMiddle;

    // MARK - constructors
    public BombButton(Context parentContext)
    {
        super(parentContext);
    }

    public BombButton(Context parentContext, int xValue, int yValue)
    {
        super(parentContext);
        this.xValue = xValue;
        this.yValue = yValue;
        this.isBomb = this.isFlagged = this.isRevealed = this.isFlipping = false;
        this.neighbouringBombCount = 0;
        this.setBackgroundResource(android.R.drawable.btn_default);
        this.getBackground().setColorFilter(getResources().getColor(R.color.button_color), PorterDuff.Mode.SRC_ATOP);
        this.setTextColor(Color.parseColor("black"));

        try
        {
            this.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/DejaVuSans.ttf"));
        }
        catch (RuntimeException re)
        {
            Log.e("Typeface cannot be made", re.getMessage());
        }

        this.toMiddle = AnimationUtils.loadAnimation(getContext(), R.anim.button_to_middle);
        this.fromMiddle = AnimationUtils.loadAnimation(getContext(), R.anim.button_from_middle);
        this.toMiddle.setAnimationListener(this);
        this.fromMiddle.setAnimationListener(this);
    }

    // MARK - instance methods
    public void clear()
    {
        this.isRevealed = this.isBomb = this.isFlagged = false;
        this.neighbouringBombCount = 0;
        this.setText("");
        this.setBackgroundResource(android.R.drawable.btn_default);
        this.getBackground().setColorFilter(getResources().getColor(R.color.button_color),
                PorterDuff.Mode.SRC_ATOP);
    }

    public void setBomb()
    {
        this.isBomb = true;
    }

    public boolean isBomb()
    {
        return this.isBomb;
    }

    public boolean isFlagged()
    {
        return this.isFlagged;
    }

    public boolean isRevealed()
    {
        return this.isRevealed;
    }

    public void incrementNeighbouringBombCount()
    {
        this.neighbouringBombCount++;
    }

    public int getNeighbouringBombCount()
    {
        return this.neighbouringBombCount;
    }

    public int getXValue()
    {
        return this.xValue;
    }

    public int getYValue()
    {
        return this.yValue;
    }

    public void removeFlipping()
    {
        this.isFlipping = false;
    }

    public void setFlagged()
    {
        isFlagged = isRevealed == isFlagged;
    }

    // MARK - animation controls
    public void startAnimation()
    {
        this.clearAnimation();
        this.setAnimation(this.toMiddle);
        this.startAnimation(this.toMiddle);
    }

    public void finalizeAnimation()
    {
        this.clearAnimation();

        if (isFlipping)
        {
            clear();
        }
        else
        {
            finishReveal();
        }

        this.setAnimation(this.fromMiddle);
        this.startAnimation(this.fromMiddle);
    }

    public void reveal()
    {
        if (neighbouringBombCount != 0 && !isRevealed)
        {
            startAnimation();
            this.setOnLongClickListener(null);
        }
        else
        {
            finishReveal();
        }
    }

    protected void finishReveal()
    {
        this.isRevealed = true;

        if (this.isBomb && this.isFlagged())
        {
            this.setText("");
            this.setBackgroundResource(R.drawable.bomb_defused);
        }
        else if (isBomb)
        {
            this.setBackgroundResource(R.drawable.bomb);
        }
        else
        {
            this.setText((this.neighbouringBombCount != 0) ? this.neighbouringBombCount + "" : "");
            this.getBackground().setColorFilter(getResources()
                            .getColor((this.neighbouringBombCount == 0) ?
                                    R.color.button_empty_color : R.color.button_used_color),
                    PorterDuff.Mode.SRC_ATOP);
        }
        this.setOnLongClickListener(null);
    }

    @Override
    public void onAnimationStart(Animation animation)
    {
        // TODO: auto-generated stub
    }

    @Override
    public void onAnimationRepeat(Animation animation)
    {
        // TODO: auto-generated stub
    }

    @Override
    public void onAnimationEnd(Animation animation)
    {
        if (animation == toMiddle)
        {
            finalizeAnimation();
        }

        clearAnimation();
    }

    public void flip()
    {
        this.isFlipping = true;
        startAnimation();
    }
}
