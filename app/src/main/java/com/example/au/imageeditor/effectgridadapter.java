package com.example.au.imageeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhishek upadhyay on 17-09-2016.
 */
public class effectgridadapter extends RecyclerView.Adapter<effectgridadapter.SimpleViewHolder>{
    private Context mContext;
    private ArrayList<String> meffect;
    Effecttable mEffecttable;
    Bitmap mBitmap;
    Bitmap mBitmap1;
    Bitmap mBitmap2;
    MainActivity mMainActivity;

    private ImageView mImageView;


    public effectgridadapter(Context context, ArrayList<String> effect, ImageView mImage) {
        this.mContext=context;
       this.mImageView=mImage;
        this.mBitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        mBitmap1=mBitmap;
        mEffecttable=new Effecttable(mContext);

       this.meffect=  effect;

    }



    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
  public final TextView effectextview;
        ImageView mImageView2;
      CardView mCardView;

        public SimpleViewHolder(View view) {
            super(view);
           effectextview = (TextView) view.findViewById(R.id.effecttext);
            mImageView2=(ImageView) view.findViewById(R.id.imageView2);
            mCardView= (CardView) view.findViewById(R.id.cv);
        }
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.mContext).inflate(R.layout.effectgrid, parent, false);
        //ImageButton mImageeffect = (ImageButton) view.findViewById(R.id.);

        return new SimpleViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        final int  position2=position+ 1;
        holder.effectextview.setText(meffect.get(position));
        holder.mImageView2.setImageBitmap(getbitmap(position));
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Snackbar snackbar = Snackbar
                        .make(holder.mCardView.getRootView(),String.valueOf(position2), Snackbar.LENGTH_LONG);

                snackbar.show();
                Log.v("working","till here0");
                mBitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                Log.v("working","till here1");
              // mBitmap= mEffecttable.doGreyscale(mBitmap);
                switch (position2){
                    case 1: mBitmap=mEffecttable.toGrayscale(mBitmap);
                        break;
                    case 2:mBitmap=mEffecttable.sharpen(mBitmap);
                        break;
                    case 3:mBitmap=mEffecttable.doHighlightImage(mBitmap);
                        break;
                    case 4 :mBitmap=mEffecttable.applyMeanRemoval(mBitmap);
                        break;
                    case 5 :mBitmap=mEffecttable.bright(mBitmap);
                        break;
                    case 6 :mBitmap=mEffecttable.sepia(mBitmap);
                        break;
                    case 7 :mBitmap=mEffecttable.Sobelh(mBitmap);
                        break;
                    case 8 :mBitmap=mEffecttable.Sobelv(mBitmap);
                        break;
                    case 9 :mBitmap=mEffecttable.flip(mBitmap);
                        break;
                    case 10 :mBitmap=mEffecttable.Invert(mBitmap);
                        break;

                }

                Log.v("working","till here2");
               mImageView.setImageBitmap(mBitmap);

            }
        });
    }

    private Bitmap getbitmap(int mPosition) {
        int position2 = mPosition + 1;
        switch (position2) {
            case 1:
                mBitmap2 = mEffecttable.toGrayscale(mBitmap1);
                break;
            case 2:
                mBitmap2 = mEffecttable.sharpen(mBitmap1);
                break;
            case 3:
                mBitmap2 = mEffecttable.doHighlightImage(mBitmap1);
                break;
            case 4:
                mBitmap2 = mEffecttable.applyMeanRemoval(mBitmap1);
                break;
            case 5:
                mBitmap2 = mEffecttable.bright(mBitmap1);
                break;
            case 6:
                mBitmap2 = mEffecttable.sepia(mBitmap1);
                break;
            case 7:
                mBitmap2 = mEffecttable.Sobelh(mBitmap1);
                break;
            case 8:
                mBitmap2 = mEffecttable.Sobelv(mBitmap1);
                break;
            case 9:
                mBitmap2 = mEffecttable.flip(mBitmap1);
                break;
            case 10:
                mBitmap2 = mEffecttable.Invert(mBitmap1);
                break;


        }
        return mBitmap2;
    }

        @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return meffect.size();
    }



}
