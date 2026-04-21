package com.natividad.song;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Controller
@RequestMapping(path="/natividad")
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @PostMapping(path="/songs")
    public ResponseEntity<?> createSong(@RequestBody Song song) throws URISyntaxException {
        Song savedSong = songRepository.save(song);
        return ResponseEntity.ok(savedSong);
    }

    // PUT by id: Update an existing song
    @PutMapping("/songs/{id}")
    public ResponseEntity<?> updateSong(@PathVariable Long id, @RequestBody Song song) {
        try {
            Song currentSong = songRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Song with id " + id + " not found."));


            currentSong.setTitle(song.getTitle());
            currentSong.setArtist(song.getArtist());
            currentSong.setAlbum(song.getAlbum());
            currentSong.setGenre(song.getGenre());
            currentSong.setUrl(song.getUrl());

            currentSong = songRepository.save(currentSong);
            return ResponseEntity.ok(currentSong);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping(path="/songss")
    public @ResponseBody Iterable<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @GetMapping(path="/songs/{id}")
    public @ResponseBody ResponseEntity<?> getSong(@PathVariable Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            return ResponseEntity.ok(song.get());
        } else {
            return ResponseEntity.badRequest().body("Song with id " + id + " not found.");
        }
    }

    @DeleteMapping(path="/songs/{id}")
    public @ResponseBody ResponseEntity<?> deleteSong(@PathVariable Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            songRepository.deleteById(id);
            return ResponseEntity.ok("Song with ID " + id + " deleted.");
        } else {
            return ResponseEntity.badRequest().body("Song with id " + id + " not found.");
        }
    }

    @GetMapping("/songs/search/{key}")
    public ResponseEntity<Iterable<Song>> searchSongs(@PathVariable String key) {
        Iterable<Song> results = songRepository
                .findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrAlbumContainingIgnoreCaseOrGenreContainingIgnoreCase(
                        key, key, key, key);
        return ResponseEntity.ok(results);
    }
}