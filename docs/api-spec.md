# Likes
## ConfirmLikeUpdate
Response: [ConfirmLikeUpdate](https://github.com/MSDO-ImageHost/Likes/blob/main/docs/api-spec.md#ConfirmLikeUpdate) Request: [UpdateLike](https://github.com/MSDO-ImageHost/Gateway/blob/main/docs/api-spec.md#ConfirmLikeUpdate)
Request: 
```json
{
    "User": "<user-id>",
    "Post_ID": "<postid>"
}
```
Response:
```json
{
    "Like status": "True/False"
}
```

## ReturnLikesForPost
Response: [ReturnLikesForPost](https://github.com/MSDO-ImageHost/Likes/blob/main/docs/api-spec.md#ReturnLikesForPost) Request: [RequestLikesForPost](https://github.com/MSDO-ImageHost/Posts/blob/main/docs/api-spec.md#RequestLikesForPost)
Request: 
```json
{
    "Post_ID": "<postid>"
}
```
Response:
```json
{
    "Like amount": "<Positive int>"
}
```


## ReturnLikesForUser
Response: [ReturnLikesForUser](https://github.com/MSDO-ImageHost/Likes/blob/main/docs/api-spec.md#ReturnLikesForUser) Request: [ReturnLikesForUser](https://github.com/MSDO-ImageHost/Accounts/blob/main/docs/api-spec.md#ReturnLikesForUser)
Request: 
```json
{
    "User": "<User_ID>"
}
```
Response:
```json
{
    "Like amount": "<Positive int>"
}
```
