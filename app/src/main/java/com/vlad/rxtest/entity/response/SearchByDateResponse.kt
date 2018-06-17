package com.vlad.rxtest.entity.response

data class SearchByDate(
        val hits: List<Hit>,
        val nbHits: Int,
        val page: Int,
        val nbPages: Int,
        val hitsPerPage: Int,
        val processingTimeMS: Int,
        val exhaustiveNbHits: Boolean,
        val query: String,
        val params: String
)

data class Author(
        val value: String,
        val matchLevel: String,
        val matchedWords: List<Any>
)

data class HighlightResult(
        val title: Title,
        val url: Url,
        val author: Author
)

data class Hit(
        val createdAt: String,
        val title: String,
        val url: String,
        val author: String,
        val points: Int,
        val storyText: Any,
        val commentText: Any,
        val numComments: Int,
        val storyId: Any,
        val storyTitle: Any,
        val storyUrl: Any,
        val parentId: Any,
        val createdAtI: Int,
        val tags: List<String>,
        val objectID: String,
        val highlightResult: HighlightResult
)

data class Title(
        val value: String,
        var matchLevel: String,
        val matchedWords: List<Any>
)

data class Url(
        val value: String,
        val matchLevel: String,
        val matchedWords: List<Any>
)