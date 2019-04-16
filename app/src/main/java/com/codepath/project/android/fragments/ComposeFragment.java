package com.codepath.project.android.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.project.android.ParseApplication;
import com.codepath.project.android.R;
import com.codepath.project.android.activities.ProductViewActivity;
import com.codepath.project.android.helpers.BitmapScaler;
import com.codepath.project.android.helpers.ThemeUtils;
import com.codepath.project.android.model.Feed;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.Review;
import com.codepath.project.android.utils.ImageUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ComposeFragment extends DialogFragment {

    @BindView(R.id.etReviewText) EditText etReviewText;
    @BindView(R.id.btnPost) Button btnPost;
    @BindView(R.id.ivComposeCancel) ImageView ivComposeCancel;
    @BindView(R.id.ivCamera) ImageView ivCamera;
    @BindView(R.id.ivGallery) ImageView ivGallery;
    @BindView(R.id.rbAverageRating) RatingBar rbAverageRating;
    @BindView(R.id.rvCapturedImages) RecyclerView rvCapturedImages;
    @BindView(R.id.ivProductImage) ImageView ivProductImage;
    @BindView(R.id.tvProductName) TextView tvProductName;

    List<ParseFile> images;
    List<Bitmap> bitmaps;

    Product product;
    ImageCaptureAdapter imageCaptureAdapter;

    private Unbinder unbinder;

    public ComposeFragment() {}

    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("Compose review", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ThemeUtils.onActivityCreateSetTheme(getActivity());
        final Context contextThemeWrapper;
        if(ParseApplication.currentPosition == 0) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_Material_Light);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_Night_Mode);
        }
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        images = new ArrayList<>();
        bitmaps = new ArrayList<>();

        ivComposeCancel.setOnClickListener(v -> closeKeyboardAndDismiss(view));

        String title = getArguments().getString("title", "Compose review");
        getDialog().setTitle(title);
        etReviewText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        Bundle bundle = this.getArguments();
        String productId = bundle.getString("productId");
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        assert productId != null;
        query.getInBackground(productId.trim(), (p, e) -> {
            if(e == null) {
                product = p;
                tvProductName.setText(p.getName());
                Picasso.with(getContext()).load(p.getImageUrl()).placeholder(R.drawable.placeholder).into(ivProductImage);
            } else {
                Toast.makeText(getActivity(), "parse error", Toast.LENGTH_SHORT).show();
            }
        });

        btnPost.setOnClickListener(arg0 -> {
            Review review = new Review();
            String reviewText = etReviewText.getText().toString();
            float rating = rbAverageRating.getRating();

            review.setText(reviewText);
            product.incrementReviewCount();
            product.setRating((int) rating);

            review.setProduct(product);
            review.setUser(ParseUser.getCurrentUser());
            if(images.size() > 0) {
                review.setImages(images);
            }
            review.setRating(Integer.toString(Math.round(rating)));
            try {
                review.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Feed feed = new Feed();
            feed.setType("addReview");
            feed.setContent(reviewText);
            feed.setFromUser(ParseUser.getCurrentUser());
            feed.setToProduct(product);
            feed.setRating(Math.round(rating));
            feed.setReview(review);
            feed.saveInBackground();
            ((ProductViewActivity)getActivity()).onNewReviewAdded();
            closeKeyboardAndDismiss(view);
        });

        etReviewText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                if(s.length() > 0 && rbAverageRating.getRating() > 0){
                    btnPost.setEnabled(true);
                } else {
                    btnPost.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rbAverageRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if(!TextUtils.isEmpty(etReviewText.getText()) && rbAverageRating.getRating() > 0){
                btnPost.setEnabled(true);
            } else {
                btnPost.setEnabled(false);
            }
        });

        ivCamera.setOnClickListener(v -> onCameraClick(v));

        ivGallery.setOnClickListener(v -> onGalleryClick(v));

        imageCaptureAdapter = new ImageCaptureAdapter(getActivity(), bitmaps);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvCapturedImages.setLayoutManager(mLayoutManager);
        rvCapturedImages.setAdapter(imageCaptureAdapter);
    }

    public void closeKeyboardAndDismiss(View view) {
        InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        dismiss();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onCameraClick(View v) {
        InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri("photo-codepath.jpg")); // set the image file name
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 1034);
        }
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onGalleryClick(View v) {
        InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, 1033);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1034) {
            if (resultCode == getActivity().RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri("photo-codepath.jpg");
                //Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                Bitmap takenImage = ImageUtils.rotateBitmapOrientation(takenPhotoUri.getPath());
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 500);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] bytearray= stream.toByteArray();

                if (bytearray != null){
                    ParseFile file = new ParseFile("abcd.jpg", bytearray);
                    images.add(file);
                    bitmaps.add(resizedBitmap);
                    imageCaptureAdapter.notifyDataSetChanged();
                }
            }
        } else {
            if (data != null) {
                Uri photoUri = data.getData();
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                    Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(selectedImage, 500);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] bytearray= stream.toByteArray();

                    if (bytearray != null){
                        ParseFile file = new ParseFile("abcd.jpg", bytearray);
                        images.add(file);
                        bitmaps.add(resizedBitmap);
                        imageCaptureAdapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public Uri getPhotoFileUri(String fileName) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = new File(
                    getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyCustomApp");
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){

            }
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ComposeFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void removeImage(int position) {
        images.remove(position);
        bitmaps.remove(position);
        imageCaptureAdapter.notifyDataSetChanged();
    }

    public class ImageCaptureAdapter  extends
            RecyclerView.Adapter<ImageCaptureAdapter.ViewHolder> {

        private List<Bitmap> mImages;
        private Context mContext;

        public ImageCaptureAdapter(Context context, List<Bitmap> images) {
            mImages = images;
            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ivCancel)
            ImageView ivCancel;
            @BindView(R.id.ivImage)
            ImageView ivImage;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public ImageCaptureAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.item_image_capture, parent, false);
            return new ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(ImageCaptureAdapter.ViewHolder viewHolder, int position) {
            Bitmap image = mImages.get(position);
            viewHolder.ivImage.setImageBitmap(image);

            viewHolder.ivCancel.setOnClickListener(v -> removeImage(position));
        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }
    }
}