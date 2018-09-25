package com.vlad.rxtest.entity.response

data class NewModel(
        val authorName: String,
        val karma: Int,
        val storyTitle: String


) {
    override fun toString(): String {
        return "authorName - $authorName; karma - $karma; storyTitle - $storyTitle"
    }
}