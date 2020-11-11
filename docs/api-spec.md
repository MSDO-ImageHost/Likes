# Likes
## ConfirmLikeUpdate
Request: [UpdateLike](GatewayLink#ConfirmLikeUpdate)  
Request: 
```json
{
    "User": "<user-id>",
    "Post_ID": "<postid>"
}
```
Response: [ConfirmLikeUpdate](LikesLink#ConfirmLikeUpdate)
```json
{
    "Like status": "True/False"
}
```

## ReturnLikesForPost
Request: [RequestLikesForPost](PostsLink#RequestLikesForPost)  
Request: 
```json
{
    "Post_ID": "<postid>"
}
```
Response: [ReturnLikesForPost](LikesLink#ReturnLikesForPost)
```json
{
    "Like amount": "<Positive int>"
}
```


## ReturnLikesForUser
Request: [ReturnLikesForUser](AccountsLink#ReturnLikesForUser)  
```json
{
    "User": "<User_ID>"
}
```
Response: [ReturnLikesForUser](LikesLink#ReturnLikesForUser)
```json
{
    "Like amount": "<Positive int>"
}
```
