package com.example.musicapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private ImageView icRotate;
    private SeekBar sbTime;
    private TextView tvBaihat;
    private TextView tvTacgia;
    private Button btnPlay;
    private int currentSongIndex = 0; // Chỉ số của bài hát hiện tại
    private TextView tvHientai;
    private TextView tvThoiluong;
    private Handler handler = new Handler();
    private boolean isSongPlaying = false;
    private String currentSongName;
    private int[] songs = {R.raw.nu_hon_bisou, R.raw.aloi2t, R.raw.danh_doi, R.raw.khong_phai_gu,
            R.raw.la_anh, R.raw.anh_luonnhuvay, R.raw.idwm, R.raw.no_love_no_live};
    private String[] songNames = {"Nụ hôn Bisou", "À Lôi", "Đánh đổi", "Không phải gu", "Là anh", "Anh luôn như vậy",
            "id1011", "No love no life"};
    private String[] artistNames = {"Mikelodic", "Double 2T", "Obito", "HIEUTHUHAI", "No Tacgia", "Bray",
        "Duong Know", "HIEUTHUHAI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các view
        icRotate = findViewById(R.id.icRotate);
        sbTime = findViewById(R.id.sbTime);
        tvBaihat = findViewById(R.id.tvBaihat);
        tvTacgia = findViewById(R.id.tvTacgia);
        btnPlay = findViewById(R.id.btnPlay);

        // Khởi tạo MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.nu_hon_bisou);
        // Xử lý sự kiện nút play/pause
        btnPlay.setOnClickListener(v -> playPauseMusic());

        Button btnPre = findViewById(R.id.btnPre);
        btnPre.setOnClickListener(v -> playPreviousSong());

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> playNextSong());

        // Xử lý sự kiện khi nhạc kết thúc
        mediaPlayer.setOnCompletionListener(mp -> playNextSong());
        tvHientai = findViewById(R.id.tvHientai);
        tvThoiluong = findViewById(R.id.tvThoiluong);
        sbTime = findViewById(R.id.sbTime);
        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                    updateSeekBarTextViews();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacksAndMessages(null); //  Tắt cập nhật tvHientai khi chạm vào SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(runnable, 1000); // Bật lại cập nhật sau khi dừng chạm vào SeekBar
            }
        });
        updateSeekBar();
        Button btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SongListActivity.class);
            intent.putExtra("songList", songs); // Chuyển danh sách ID nguồn của bài hát
            startActivityForResult(intent, 1);
        });
        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> showExitDialog());
    }

    private void playPauseMusic() {
        if (isPlaying) {
            mediaPlayer.pause();
            icRotate.clearAnimation();
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.play_button), null, null, null);
            isSongPlaying = false;
            handler.removeCallbacksAndMessages(null); // Loại bỏ tất cả các callback trong handler
        } else {
            mediaPlayer.start();
            updateSongTitle();
            Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
            icRotate.startAnimation(rotateAnimation);
            btnPlay.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.pause_button), null, null, null);
            isSongPlaying = true;
            updateSeekBarTextViews(); // Cập nhật lần đầu tiên
            handler.postDelayed(runnable, 1000); // Gọi lại sau mỗi giây
        }
        isPlaying = !isPlaying;
    }
    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
        } else {
            // Nếu đang ở bài hát đầu tiên, chuyển đến bài hát cuối cùng
            currentSongIndex = songs.length - 1;
        }
        changeSong();
    }
    private void playNextSong() {
        if (currentSongIndex < songs.length - 1) {
            currentSongIndex++;
        } else {
            // Nếu đang ở bài hát cuối cùng, chuyển đến bài hát đầu tiên
            currentSongIndex = 0;
        }
        changeSong();
    }
    private void changeSong() {
        if (isPlaying) {
            mediaPlayer.stop();
            isPlaying = false;
        }
        mediaPlayer.release(); // Giải phóng tài nguyên trước khi khởi tạo mới
        mediaPlayer = MediaPlayer.create(this, songs[currentSongIndex]);

        // Đặt lại sự kiện khi nhạc kết thúc
        mediaPlayer.setOnCompletionListener(mp -> playNextSong());
        updateSongTitle();
        playPauseMusic();
    }
    private void updateSongTitle() {
        currentSongName = songNames[currentSongIndex];
        String currentArtist = artistNames[currentSongIndex];

        tvBaihat.setText(currentSongName);
        tvTacgia.setText(currentArtist);
    }
    private void updateSeekBar() {
        sbTime.setMax(mediaPlayer.getDuration() / 1000);
        new Handler().postDelayed(() -> {
            if (mediaPlayer != null) {
                sbTime.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                updateSeekBar();
            }
        }, 1000);
    }
    private void updateSeekBarTextViews() {
        if (isSongPlaying) {
            // Hiển thị thời gian hiện tại
            int currentTimeInSeconds = mediaPlayer.getCurrentPosition() / 1000;
            tvHientai.setText(formatTime(currentTimeInSeconds));
        }

        // Hiển thị thời lượng bài hát
        int songDurationInSeconds = mediaPlayer.getDuration() / 1000;
        tvThoiluong.setText(formatTime(songDurationInSeconds));
    }
    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateSeekBarTextViews();
            if (isSongPlaying) {
                handler.postDelayed(this, 1000);
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int selectedSongIndex = data.getIntExtra("selectedSongIndex", 0);
            currentSongIndex = selectedSongIndex;
            changeSong();
        }
    }
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn thoát khỏi ứng dụng?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Kết thúc ứng dụng
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Đóng hộp thoại
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
