package me.iwf.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.R;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.utils.AndroidLifecycleUtils;

/**
 * Created by donglua on 15/6/21.
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private List<String> selectPaths = new ArrayList<>();
    private List<String> paths = new ArrayList<>();
    private RequestManager mGlide;
    private Context context;
    private OnItemCheckListener onItemCheckListener = null;
    private OnSelectChangeListener onSelectChangeListener = null;
    private List<Photo> photos;

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public PhotoPagerAdapter(RequestManager glide, List<String> paths, List<String> selectPaths) {
        this.paths = paths;
        this.selectPaths = selectPaths;
        this.mGlide = glide;
    }

    public List<String> getSelectPaths() {
        return selectPaths;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        context = container.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.__picker_picker_item_pager, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);
        final ImageView imageCheck = (ImageView) itemView.findViewById(R.id.v_selected);

        final String path = paths.get(position);
        imageCheck.setSelected(selectPaths.contains(path));
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(context);

        if (canLoadImage) {
            RequestOptions options = new RequestOptions()
//                    .centerCrop()
//                    .override(800, 800)
                    .placeholder(R.drawable.__picker_ic_photo_black_48dp)
                    .error(R.drawable.__picker_ic_broken_image_black_48dp);
            mGlide.load(uri)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing()) {
                        ((Activity) context).onBackPressed();
                    }
                }
            }
        });

        final Photo photo = photos.get(position);

        imageCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnable = true;
                ImageView img = imageCheck;
                if (onItemCheckListener != null) {
                    isEnable = onItemCheckListener.onItemCheck(OnItemCheckListener.PhotoPagerAdapter, position, photo,
                            selectPaths.size() + (isSelected(photo) ? -1 : 1));
                }
                if (isEnable) {
                    toggleSelection(imageCheck, photo);
                }
                notifyDataSetChanged();
            }
        });

        container.addView(itemView);
        return itemView;
    }

    private void toggleSelection(ImageView imageView, Photo photo) {
        if (selectPaths.contains(photo.getPath())) {
            selectPaths.remove(photo.getPath());
        } else {
            selectPaths.add(photo.getPath());
        }
        onSelectChangeListener.getSelectNum(selectPaths.size());
    }

    private boolean isSelected(Photo photo) {
        return selectPaths.contains(photo.getPath());
    }


    @Override
    public int getCount() {
        return paths.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
                Glide.get(context).clearMemory();
            }
        });
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
