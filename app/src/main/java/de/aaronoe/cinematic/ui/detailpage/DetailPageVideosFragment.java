package de.aaronoe.cinematic.ui.detailpage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.cinematic.BuildConfig;
import de.aaronoe.cinematic.movies.MovieItem;
import de.aaronoe.cinematic.movies.VideoItem;
import de.aaronoe.cinematic.movies.VideoResponse;
import de.aaronoe.cinematic.CinematicApp;
import de.aaronoe.cinematic.R;
import de.aaronoe.cinematic.model.remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.makeramen.roundedimageview.RoundedImageView.TAG;

public class DetailPageVideosFragment extends Fragment {


    private final static String API_KEY = BuildConfig.MOVIE_DB_API_KEY;
    @BindView(R.id.recyclerView_videos) RecyclerView mRecyclerView;
    @BindView(R.id.video_tv_error_message_display) TextView mErrorMessageTextView;
    @BindView(R.id.video_pb_loading_indicator) ProgressBar mProgressBar;
    VideoAdapter mVideoAdapter;
    MovieItem mMovieItem;

    @Inject ApiInterface apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_page_videos, container, false);
        ButterKnife.bind(this, rootView);

        ((CinematicApp) getActivity().getApplication()).getNetComponent().inject(this);

        mMovieItem = getArguments().getParcelable("thisMovie");

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mVideoAdapter = new VideoAdapter(getActivity());
        mRecyclerView.setAdapter(mVideoAdapter);

        downloadVideoData();
        return rootView;
    }


    private void downloadVideoData() {

        mProgressBar.setVisibility(View.VISIBLE);
        int movieId = mMovieItem.getId();
        Log.d(TAG, "downloadVideoData() called" + movieId) ;
        Call<VideoResponse> call = apiService.getVideos(movieId, API_KEY);

        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                if (response == null || response.body() == null || response.body().getResults() == null) {
                    return;
                }

                List<VideoItem> videoItems = response.body().getResults();

                if (videoItems != null) {
                    showVideoView();
                    mVideoAdapter.setVideoData(videoItems);
                } else {
                    showErrorMessage();
                }

            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                showErrorMessage();
            }
        });


    }


    private void showVideoView() {
        /* First, make sure the error is invisible */
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }


    public static android.support.v4.app.Fragment newInstance(MovieItem mCurrentMovie) {
        DetailPageVideosFragment myDetailFragment = new DetailPageVideosFragment();

        Bundle movie = new Bundle();
        movie.putParcelable("thisMovie", mCurrentMovie);
        myDetailFragment.setArguments(movie);

        return myDetailFragment;
    }

}
