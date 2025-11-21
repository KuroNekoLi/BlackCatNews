# In-App Review 行為說明

- **觸發情境**：使用者離開 `ArticleDetail` 頁面時，系統會先將 `articleReadCount` +1。當累積閱讀次數 >= 3，且從未呼叫過 in-app review 時，才會向 Google Play 請求顯示官方評分 dialog。
- **本地旗標**：`articleReadCount` 與 `hasAskedForReview` 透過 DataStore 儲存在裝置端。只要成功呼叫一次 `launchReviewFlow` 並等待 Task 完成，就會把 `hasAskedForReview` 設為 `true`，後續不再打擾使用者。
- **API 限制**：Google Play In-App Review API 不會透露使用者是否真的完成評分，也不允許客製 fallback dialog。我們依照官方建議，只要安靜地呼叫 `requestReviewFlow`/`launchReviewFlow`，若 Task 失敗就直接結束，不顯示額外 UI。
- **測試方式**：請將 APK/Bundle 釋出到 Internal testing 或 Internal App Sharing，並使用已在測試名單中的帳號。只有在 Google 配額允許時才會顯示真實 dialog，因此請透過日誌確認 `maybeRequestReview` 流程與 DataStore 旗標是否更新。
