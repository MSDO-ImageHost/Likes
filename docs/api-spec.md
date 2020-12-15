# API Reference
## ConfirmLikeUpdate
Request UpdateLike; Sent by [Gateway](../../../../Gateway):
```json
{
    "user": "<user-id>",
    "post_ID": "<postid>"
}
```
Response ConfirmLikeUpdate; Received by [Gateway](../../../../Gateway):
```json
{
    "like_status": "True/False"
}
```

## ReturnLikesForPost
Request RequestLikesForPost; Sent by [Posts](../../../../Posts):
```json
{
    "post_id": "<postid>"
}
```
Response ReturnLikesForPost; Received by [Posts](../../../../Posts):
```json
{
    "like_amount": "<Positive int>"
}
```
## ReturnLikeStatus
Request RequestLikeStatus; Sent by [Posts](../../../../Posts):
```json
{
    "post_id": "<postid>"
}
```
Response ReturnLikeStatus; Received by [Posts](../../../../Posts):
```json
{
    "like_status": "True/False"
}
```
