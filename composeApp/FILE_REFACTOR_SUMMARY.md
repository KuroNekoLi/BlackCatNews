# 檔案重構總結

## 需要執行的操作

### 1. 移動 model/ 下的文件到 domain/model/
- NewsItem.kt → domain/model/Article.kt (重命名)
- ArticleDetail.kt → domain/model/ArticleDetail.kt
- ArticleData.kt → 需要檢查內容後決定

### 2. 移動 components/HtmlText.kt
- → ui/components/HtmlText.kt

### 3. 移動 parser/HtmlParser.kt  
- → utils/HtmlParser.kt

### 4. 刪除 examples/ 目錄
- HtmlTextExample.kt (示例代碼)

### 5. 創建 DTO 和 API Service
- data/remote/dto/AiArticleDto.kt
- data/remote/api/NewsApiService.kt
- data/remote/api/HttpClientFactory.kt

準備好後請告訴我，我會開始執行這些操作。
