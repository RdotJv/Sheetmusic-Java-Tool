package com.example.repositories;

import com.example.services.WsService.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SheetmusicRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void addSong(Song song) {
        String sql = "INSERT INTO sheet_data VALUES (NULL, ?, ?, ?)";
        jdbcTemplate.update(sql, song.songName(), song.pdfUrl(), song.composerName());
    }

    public List<Song> allSongs() {
        String sql = "SELECT * FROM sheet_data";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Song(rs.getInt("sheetId"), rs.getString("composerName"), rs.getString("sheetName"), rs.getString("sheetUrl"));
        });
    }
}
