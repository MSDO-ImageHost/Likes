CREATE DATABASE IF NOT EXISTS Likes;
USE Likes;
CREATE TABLE IF NOT EXISTS Likes (
                                     postID varchar(255) NOT NULL,
                                     userID varchar(255) NOT NULL,
                                     Liked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)