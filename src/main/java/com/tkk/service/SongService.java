package com.tkk.service;

import com.ibm.icu.text.Transliterator;
import com.tkk.dto.SongIndex;
import com.tkk.dto.SongIndexScore;
import com.tkk.entity.Song;
import com.tkk.repo.SongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class SongService {

    private final SongRepository songRepository;

    private static final Transliterator TELUGU_TRANSLITERATOR;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    private Map<Character, List<SongIndex>> groupSongsBy(Function<SongIndex, String> fieldExtractor) {

        List<SongIndex> songs = songRepository.findAllByList();

        Map<Character, List<SongIndex>> grouped = new HashMap<>();

        for (SongIndex song : songs) {
            String value = fieldExtractor.apply(song);
            if (value == null || value.isEmpty()) {
                continue;
            }
            char first = value.charAt(0);
            grouped.computeIfAbsent(first, k -> new ArrayList<>()).add(song);
        }

        grouped.forEach((key, list) ->
                list.sort(Comparator.comparing(fieldExtractor))
        );

        return grouped.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll
                );
    }

    /**
    * Gets the list of titles in alpha order
    * @return Map<Character, List<SongIndex></SongIndex>
    * @since 1.1
    * */
    public Map<Character, List<SongIndex>> getTitles() {
        return groupSongsBy(SongIndex::getTitle);
    }

    /**
     * Gets the list of transliteration in alpha order
     * @return Map<Character, List<SongIndex></SongIndex>
     * @since 1.1
     * */
    public Map<Character, List<SongIndex>> getTransliterations() {
        return groupSongsBy(SongIndex::getTransliteration);
    }

    public Optional<Song> songById(Long id){
        return songRepository.findById(id);
    }

    public List<SongIndex> lastUploads(int limit){
        return songRepository.findLast(PageRequest.of(0,limit));
    }

    public List<SongIndexScore> search(String term){
        return songRepository.search(term);
    }

    public String saveSong(Song song) {
        // 1. Trim hidden spaces (e.g., if the user accidentally hits space bar at the end)
        String titleToCheck = song.getTitle() != null ? song.getTitle().trim() : "";

        // 2. Check if the Telugu title already exists
        boolean exists = songRepository.existsByTitle(titleToCheck);

        if (exists) {
            return "error";
        }

        // 3. Save and return success
        String englishTransliteration = SongService.toEnglish(titleToCheck);
        song.setTransliteration(englishTransliteration);
        songRepository.save(song);
        return "SUCCESS";
    }

    // This block builds and registers the engine correctly on startup!
    static {
        Transliterator base = Transliterator.getInstance("Telugu-Latin; Latin-ASCII");

        // THE FIX: Reordered so 'CH' is checked before 'C' to prevent rule masking
        String customRules = "ch > chh; c > ch; Ch > Chh; CH > CHH; C > Ch; ";

        Transliterator custom = Transliterator.createFromRules("Fix-CH", customRules, Transliterator.FORWARD);

        // Register the custom transliterator before referencing its ID
        Transliterator.registerInstance(custom);

        TELUGU_TRANSLITERATOR = Transliterator.getInstance(base.getID() + ";" + custom.getID());
    }

    /**
     * Converts Telugu text to Natural English Transliteration (Title Case).
     * @param teluguText The string in Telugu script
     * @return The transliterated English string (e.g., "Chintenduku Miku Digulenduku")
     */
    public static String toEnglish(String teluguText) {
        // Handle null or empty strings safely
        if (teluguText == null || teluguText.trim().isEmpty()) {
            return "";
        }

        // 1. Get the raw lowercase transliteration
        String rawEnglish = TELUGU_TRANSLITERATOR.transliterate(teluguText);

        // 2. Convert to Title Case and return
        return capitalizeWords(rawEnglish);
    }

    /**
     * Helper method to capitalize the first letter of every word.
     */
    private static String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder capitalizedResult = new StringBuilder();
        // Split the sentence by spaces
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize the first letter and make the rest lowercase
                capitalizedResult.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        // Return the final string, trimming the trailing space at the end
        return capitalizedResult.toString().trim();
    }

    public void remove(long id){
        songRepository.deleteById(id);
    }
}