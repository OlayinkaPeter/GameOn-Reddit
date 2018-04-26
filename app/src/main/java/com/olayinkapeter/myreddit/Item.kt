package com.olayinkapeter.myreddit

/**
 * Created by Olayinka_Peter on 4/25/2018.
 */
class Item {
    var title: String = ""
    var score: String = ""
    var subreddit: String = ""
    var url: String = ""

    constructor(title: String, score: String, subreddit: String, url: String) {
        this.title = title
        this.score = score
        this.subreddit = subreddit
        this.url = url
    }
}