package lab.infoworks.starter.ui.activities.riderDetail.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lab.infoworks.libshared.domain.model.RiderPhoto;
import lab.infoworks.libshared.domain.shared.FileManager;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;
import lab.infoworks.starter.operations.EncryptedFileFetchingService;
import lab.infoworks.starter.ui.activities.riderDetail.RiderDetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.riderPhotosRecycler)
    RecyclerView riderPhotosRecycler;
    PhotosAdapter riderPhotosAdapter;

    public PhotosFragment() {
        // Required empty public constructor
    }

    public static PhotosFragment newInstance(Integer userid) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putInt("userid", userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //TODO:
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos_recycler, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            //TODO:
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        NotificationCenter.addObserverOnMain(getActivity(), EncryptedFileFetchingService.ENCRYPTED_SERVICE_COMPLETE, (intent, data) -> {
            //
            String albumName = data.getStringExtra("albumName");
            ViewModelProviders.of(this).get(RiderDetailViewModel.class)
                    .getPhotos().observe(getViewLifecycleOwner(), (riderPhotos) -> {
                //Read Bitmap from disk assign to Photo.photo
                FileManager fileManager = new FileManager(getContext());
                //File albumDir = new File(getContext().getFilesDir(), albumName);
                File albumDir = fileManager.createFolder(albumName);
                for (RiderPhoto photo : riderPhotos) {
                    /*File imgFile = new File(dir, photo.getImageName());
                    if (imgFile.exists()){
                        try {
                            FileInputStream fos = new FileInputStream(imgFile);
                            photo.setPhoto(AssetManager.readAsImage(fos, 0));
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/
                    try {
                        photo.setPhoto(fileManager.readBitmap(albumDir, photo.getImageName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //
                PhotosAdapter adapter = new PhotosAdapter(riderPhotos);
                riderPhotosRecycler.setAdapter(adapter);
                riderPhotosAdapter = adapter;
                riderPhotosRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.HORIZONTAL, false));
                //
            });
            //
            int userid = Integer.valueOf(data.getStringExtra("userid"));
            ViewModelProviders.of(this)
                    .get(RiderDetailViewModel.class)
                    .findPhotosBy(userid);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.removeObserver(getActivity(), EncryptedFileFetchingService.ENCRYPTED_SERVICE_COMPLETE);
    }
}