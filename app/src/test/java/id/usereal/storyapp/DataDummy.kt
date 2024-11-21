package id.usereal.storyapp

import id.usereal.storyapp.data.model.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                id = i.toString(),
                createdAt = "2024-11-20T10:00:00Z",
                name = "Name $i",
                description = "Description for item $i",
            )
            items.add(quote)
        }
        return items
    }
}