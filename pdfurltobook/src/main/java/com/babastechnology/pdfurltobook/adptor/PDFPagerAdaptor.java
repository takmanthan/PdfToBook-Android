package com.babastechnology.pdfurltobook.adptor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.babastechnology.pdfurltobook.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class PDFPagerAdaptor extends PagerAdapter {

    Activity mContext;
    ArrayList<Bitmap> imageList;

    public PDFPagerAdaptor(Activity context, ArrayList<Bitmap> imageList) {
        mContext = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view_pager, container, false);
        PhotoView pdfImageView = itemView.findViewById(R.id.pdf_image) ;

        pdfImageView.setImageBitmap(imageList.get(position));
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
