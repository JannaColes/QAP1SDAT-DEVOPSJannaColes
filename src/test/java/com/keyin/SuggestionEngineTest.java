package com.keyin;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class SuggestionEngineTest {
    private SuggestionEngine suggestionEngine = new SuggestionEngine();

    @Mock
    private SuggestionsDatabase mockSuggestionDB;
    private boolean testInstanceSame = false;

    @Test
    public void testGenerateSuggestions() throws Exception {
        suggestionEngine.loadDictionaryData( Paths.get( ClassLoader.getSystemResource("words.txt").getPath()));

//        Assertions.assertTrue(testInstanceSame);
        Assertions.assertTrue(suggestionEngine.generateSuggestions("hellw").contains("hello"));
    }

    @Test
    public void testGenerateSuggestionsFail() throws Exception {
        suggestionEngine.loadDictionaryData( Paths.get( ClassLoader.getSystemResource("words.txt").getPath()));

        testInstanceSame = true;
        Assertions.assertTrue(testInstanceSame);
        Assertions.assertFalse(suggestionEngine.generateSuggestions("hello").contains("hello"));
    }

    @Test
    public void testSuggestionsAsMock() {
        Map<String,Integer> wordMapForTest = new HashMap<>();

        wordMapForTest.put("test", 1);

        Mockito.when(mockSuggestionDB.getWordMap()).thenReturn(wordMapForTest);

        suggestionEngine.setWordSuggestionDB(mockSuggestionDB);

        Assertions.assertFalse(suggestionEngine.generateSuggestions("test").contains("test"));

        Assertions.assertTrue(suggestionEngine.generateSuggestions("tes").contains("test"));
    }


    @Test
    public void testKnownMethod() throws Exception {
        suggestionEngine.loadDictionaryData(Paths.get(ClassLoader.getSystemResource("words.txt").getPath()));
        Assertions.assertTrue(suggestionEngine.generateSuggestions("known").isEmpty());
    }

    @Test
    public void testEmptyDictionary() {
        Assertions.assertTrue(suggestionEngine.generateSuggestions("hello").isEmpty());
    }

    @Test
    public void testCaseInsensitivity() throws Exception {
        suggestionEngine.loadDictionaryData(Paths.get(ClassLoader.getSystemResource("words.txt").getPath()));
        Assertions.assertEquals("", suggestionEngine.generateSuggestions("HELLO"));
    }

    @Test
    public void testWordNotInDictionary() {
        Assertions.assertTrue(suggestionEngine.generateSuggestions("abcdefg").isEmpty());
    }

    @Test
    public void testNonAlphabeticCharacters() {
        Assertions.assertTrue(suggestionEngine.generateSuggestions("he11o").isEmpty());
        Assertions.assertTrue(suggestionEngine.generateSuggestions("wo@rd").isEmpty());
    }

    @Test
    public void testMultipleKnownEdits() throws Exception {
        suggestionEngine.loadDictionaryData(Paths.get(ClassLoader.getSystemResource("words.txt").getPath()));
        Assertions.assertTrue(suggestionEngine.generateSuggestions("helo").contains("hello"));
        Assertions.assertTrue(suggestionEngine.generateSuggestions("spel").contains("spell"));
    }

    @Test
    public void testWordsWithSpaces() {
        Assertions.assertTrue(suggestionEngine.generateSuggestions("he llo").isEmpty());
        Assertions.assertTrue(suggestionEngine.generateSuggestions("wo rd").isEmpty());
    }
}
