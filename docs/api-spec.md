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
Request RequestLikesForPost; Sent by [Gateway](../../../../Gateway):
```json
{
    "Post_ID": "<postid>"
}
```
Response ReturnLikesForPost; Received by [Gateway](../../../../Gateway):
```json
{
    "Like amount": "<Positive int>"
}
```
