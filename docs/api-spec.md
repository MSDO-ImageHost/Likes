# API Reference
## ConfirmLikeUpdate
Request UpdateLike; Sent by [Gateway](../../../../Gateway):
```json
{
    "User": "<user-id>",
    "Post_ID": "<postid>"
}
```
Response ConfirmLikeUpdate; Received by [Gateway](../../../../Gateway):
```json
{
    "Like status": "True/False"
}
```

## ReturnLikesForPost
Request RequestLikesForPost; Sent by [Posts](../../../../Posts):
```json
{
    "Post_ID": "<postid>"
}
```
Response ReturnLikesForPost; Received by [Posts](../../../../Posts):
```json
{
    "Like amount": "<Positive int>"
}
```
