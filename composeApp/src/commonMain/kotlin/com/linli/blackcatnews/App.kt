package com.linli.blackcatnews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import blackcatnews.composeapp.generated.resources.Res
import blackcatnews.composeapp.generated.resources.compose_multiplatform
import com.linli.blackcatnews.components.HtmlText
import com.linli.blackcatnews.utils.fromJsonHtml
import com.linli.blackcatnews.utils.wrapHtml

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        // 示例：JSON 格式的 HTML（带有转义字符）
        val jsonHtml = """
<article>\n<h1>中國新科技簽證引起印度關注 - 當地人不滿</h1>\n<figure>\n<img src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/1c41/live/3613a190-9e94-11f0-9f70-63cdd409bfce.jpg.webp\" alt=\"Getty Images 中國移民檢查站的標誌上寫著'外國人'，箭頭指向外國訪客的通道\">\n<figcaption>Getty Images</figcaption>\n</figure>\n<p>當中國在八月首次宣布針對科學和技術領域外國專業人士的新簽證時，幾乎未引起注意。</p>\n<p>但這個於週三生效的K簽證，上週被一個印度媒體稱為\"中國的H-1B\"後，迅速成為公眾焦點——這是指美國針對技術工人的簽證，上個月被唐納德·特朗普的行政命令針對。印度人在H-1B計劃中占主導地位，近年來佔受益者的70%以上。</p>\n<p>印度媒體的報導在中國廣泛傳播，引發了公眾對於是否擴大給外國人的福利會增加在疲軟就業市場中的競爭的擔憂——而中國傳統上並不是外國專業人士的主要移民目的地。</p>\n<p>儘管目前尚不清楚該簽證是否真的允許外國人在中國工作，或僅僅是讓他們更容易進入中國，但這並未阻止數萬名中國社交媒體用戶批評該計劃。</p>\n<p>\"我們有這麼多學士學位持有者，更不用說還有更多的碩士和博士學位持有者。我們已經有過剩的國內人才——現在你還要引進外國大學畢業生？\" 一條評論寫道。</p>\n<p>“有這麼多新計劃推動我們的大學生相互競爭，但最終，沒有什麼能比得上外國護照，”另一位微博用戶寫道。</p>\n<p>其他人談論當局是否能引進高標準的人才，並質疑外國人是否能適應中國大陸的生活，提到語言障礙和中國嚴格控制的政治體系。</p>\n<p>評論中還有一波針對印度國民的仇外和種族主義言論。</p>\n<p>反彈如此激烈，以至於國家媒體介入試圖平息局勢。</p>\n<p>週一，《環球時報》發表評論，倡導該計劃是\"讓世界看到一個更加開放和自信的中國的新時代\"的機會。</p>\n<p>週二，《人民日報》發表評論，標題為\"誤解K簽證只會誤導公眾\"。</p>\n<p>\"隨著中國走上全球舞台，它比以往任何時候都更渴望人才，\"文章補充道。</p>\n<h2>K簽證是什麼？</h2>\n<p>目前尚不清楚該計劃的全部內容，但中國政府表示，它適用於從事STEM領域——科學、技術、工程和數學的人士。</p>\n<p>當局將其描述為\"與教育、科學和技術、文化以及創業和商業活動相關的交流\"的簽證。</p>\n<p>在八月的政府媒體發布中，表示K簽證的申請者應該是\"從中國或國外知名大學或研究機構畢業，擁有STEM領域學士或更高學位，或在這些機構任教或從事研究工作的人\"。</p>\n<p>未提供年齡要求或哪些大學符合該計劃的進一步細節。</p>\n<p>值得注意的是，外國專業人士不需要當地雇主的支持即可獲得簽證——並且在入境次數、有效期和停留時間方面享有更多靈活性。</p>\n<p>而國家媒體在試圖緩解公眾焦慮的同時，並未明確說明簽證涵蓋的具體活動範圍，未能回答許多人心中的真正問題——它是否允許合格的外國人在中國工作？</p>\n<p>本週早些時候，《環球時報》在其文章中指出，K簽證不會與H-1B相同，稱其\"不是簡單的工作許可證\"。</p>\n<p>《人民日報》也表示，該簽證將\"為年輕的外國科技專業人士在中國工作和生活提供便利\"——但強調\"不應等同於移民\"。</p>\n<p>中國外交部表示，更多關於簽證的細節將由中國駐外使領館發布，未具體說明時間表。</p>\n<h2>中國的雄心和限制</h2>\n<p>顯而易見的是，隨著美國從其作為國際人才和訪客的首選目的地的地位退縮，中國正在抓住這一勢頭。</p>\n<p>K簽證的正式啟動——儘管時間是在兩個月前設定的——恰逢特朗普政府大幅提高H-1B計劃的申請費用，此舉在印度和中國等國家引發了強烈反響——這兩個國家是美國技術工人的最大來源。</p>\n<p>這只是中國吸引外國人到該國的更廣泛努力的最新一步——無論是旅遊、研究還是商業。</p>\n<p>截至七月，中國已與75個國家達成免簽協議，以便外國遊客更容易訪問該國。其吸引頂尖學者的努力已經導致一些知名學者離開美國機構並加入中國大學。</p>\n<p>《人民日報》的評論寫道：\"在一些國家轉向內部並排斥國際人才的時候，中國敏銳地抓住了這一重要機會，並迅速推出了相關政策。\"</p>\n<p>但專家表示，該計劃並非沒有其限制。</p>\n<p>根據商業情報平台Asia Briefing的編輯Giulia Interesse的說法，網絡上的反彈反映了中國對於被認為是對外國人優惠待遇的公眾審查和批評的模式。</p>\n<p>雖然社交媒體上的話語可能不能完全代表廣大公眾的情緒，但這場爭議突顯出\"實施不僅僅是監管設計的問題，還涉及公眾溝通和國內共識的建立，\"她說。</p>\n<p>語言是另一個障礙。許多近年來離開美國前往中國的研究人員和學者是華裔，並且能流利使用普通話。</p>\n<p>但對於更廣泛的外國人才來說，與中國同事的交流仍然是一個挑戰——這是雇主和員工都需要解決的問題。</p>\n<p>然而，更大的擔憂是，外國科技專業人士是否能適應中國嚴格控制的政治環境，新加坡南洋理工大學助理教授Stefanie Kam說。</p>\n<p>\"創造力和創新在開放和自由的環境中蓬勃發展，正如我們在美國和許多歐洲國家的案例中看到的那樣。但在中國的當前軌跡下，我們看到的是相反的情況，\"她告訴BBC。</p>\n<p>這些外國專業人士是否會在中國找到\"創造力和創新的空間\"，仍然是那些考慮搬遷的人面臨的關鍵問題。</p>\n</article>""".trimIndent()
        val origin = """
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="utf-8" />
              <meta name="viewport" content="width=device-width,initial-scale=1" />
              <title>Why I want Nike, Adidas and Puma to sell single shoes</title>
              <style>
                :root{
                  --max-width:900px;
                  --padding:20px;
                  --bg:#ffffff;
                  --text:#111;
                  --muted:#666;
                  --serif: "Georgia", "Times New Roman", serif;
                  --sans: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial;
                }
                html,body{height:100%;margin:0;background:var(--bg);color:var(--text);font-family:var(--sans);-webkit-font-smoothing:antialiased}
                .wrap{max-width:var(--max-width);margin:28px auto;padding:var(--padding)}
                header h1{font-size:1.6rem;margin:0 0 12px 0;line-height:1.2}
                .byline{font-size:0.95rem;color:var(--muted);margin-bottom:18px}
                article p{margin:14px 0;font-size:1rem;line-height:1.6}
                figure{margin:18px 0}
                img{max-width:100%;height:auto;display:block;border-radius:6px}
                figcaption{font-size:0.85rem;color:var(--muted);margin-top:6px}
                h2{margin-top:22px;margin-bottom:10px;font-size:1.15rem}
                .lead{font-weight:600}
                a{color:inherit;text-decoration:underline}
              </style>
            </head>
            <body>
              <div class="wrap" role="main">
                <header>
                  <h1>Why I want Nike, Adidas and Puma to sell single shoes</h1>
                  <!-- byline 已移除（僅保留標題 / 內文 / 圖片） -->
                </header>

                <article>
                  <figure>
                    <img
                      srcset="https://ichef.bbci.co.uk/news/240/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp 240w,
                              https://ichef.bbci.co.uk/news/320/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp 320w,
                              https://ichef.bbci.co.uk/news/480/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp 480w,
                              https://ichef.bbci.co.uk/news/640/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp 640w,
                              https://ichef.bbci.co.uk/news/800/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp 800w,
                              https://ichef.bbci.co.uk/news/1024/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp 1024w,
                              https://ichef.bbci.co.uk/news/1536/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp 1536w"
                      src="https://ichef.bbci.co.uk/news/480/cpsprodpb/122b/live/c89514c0-9aef-11f0-89b4-1b2d09c610c7.png.webp"
                      alt="BBC Morning Live Stef Reid standing outside presenting for BBC Morning on a sunny day. She has brown hair out is wearing a green jumper"
                    />
                    <figcaption>BBC Morning Live</figcaption>
                  </figure>

                  <p class="lead">Stef Reid grew up as a sports-obsessed child, dreaming of playing rugby internationally. But on the eve of her 16th birthday, her life changed forever.</p>

                  <p>Severe propeller lacerations from a horrific boat accident left doctors with no choice but to amputate her right foot.</p>

                  <p>Initially she continued to pursue rugby, but her agility and speed weren't the same and she grew tired of comparing her performance to when she had both feet.</p>

                  <p>"I didn't want to give up on my goal just because I was an amputee, but life is constantly changing and we have to update our goals instead of forcing them to work in a reality that no longer exists," she says.</p>

                  <p>So she moved on from rugby and took up athletics. Using a light, springy carbon-fibre blade on her right leg, she eventually became a world champion Paralympic long jumper and sprinter for both Canada and Great Britain - winning medals, breaking records and receiving an MBE.</p>

                  <p>She's now retired from professional sport, but has shown a similar adaptability and resilience in her new career which includes acting, modelling and broadcasting including competing in Dancing on Ice.</p>

                  <p>Now she's campaigning for big brands such as Nike, Adidas and Puma to sell single shoes rather than pairs.</p>

                  <p>In part it's about cost - high-end running shoes cost around £200, and when she was competing she only needed one but had to buy two, meaning she wasted hundreds of pounds on shoes she never wore.</p>

                  <p>But her biggest motivation - is the principle. Many of the big brands proudly display models with blades in their shop windows, but don't sell shoes individually for athletes like Stef.</p>

                  <p>"I loved the bladed mannequins, and I wish 15-year-old Stef had seen those.</p>

                  <p>"But now I want retailers to match their inclusive imagery with the actual buying experience".</p>

                  <p>Stef isn't alone. While she wears two shoes with her day-to-day prosthetic, many other amputees don't.</p>

                  <p>And beyond disability, thousands of people whose feet are different sizes are also affected by having to buy two shoes of the same size rather than two singles.</p>

                  <figure>
                    <img
                      srcset="https://ichef.bbci.co.uk/news/240/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp 240w,
                              https://ichef.bbci.co.uk/news/320/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp 320w,
                              https://ichef.bbci.co.uk/news/480/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp 480w,
                              https://ichef.bbci.co.uk/news/640/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp 640w,
                              https://ichef.bbci.co.uk/news/800/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp 800w,
                              https://ichef.bbci.co.uk/news/1024/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp 1024w,
                              https://ichef.bbci.co.uk/news/1536/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp 1536w"
                      src="https://ichef.bbci.co.uk/news/480/cpsprodpb/6dc9/live/43b4d2b0-9ad9-11f0-9633-b72c60b89123.jpg.webp"
                      alt="Stef Reid A full-body shot of Stef Reid in her GB Paralympic kit. She is white, has brown hair and her running blade is clearly visible"
                    />
                    <figcaption>Stef Reid</figcaption>
                  </figure>

                  <p>Nike launched a single shoe programme last year, letting customers buy one shoe at half price at select stores. However this is not clearly advertised, requires the customer to contact the care support team and is not available online.</p>

                  <p>When Stef raised the issue, she was not pointed to the scheme but instead offered a one-time 15% discount. "That's not much use in the long term, as I'm always going to have one foot".</p>

                  <figure>
                    <img
                      srcset="https://ichef.bbci.co.uk/news/240/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp 240w,
                              https://ichef.bbci.co.uk/news/320/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp 320w,
                              https://ichef.bbci.co.uk/news/480/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp 480w,
                              https://ichef.bbci.co.uk/news/640/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp 640w,
                              https://ichef.bbci.co.uk/news/800/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp 800w,
                              https://ichef.bbci.co.uk/news/1024/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp 1024w,
                              https://ichef.bbci.co.uk/news/1536/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp 1536w"
                      src="https://ichef.bbci.co.uk/news/480/cpsprodpb/8419/live/53d5e480-9ada-11f0-b42e-175c1063278e.png.webp"
                      alt="BBC Morning Live A mannequin with a running blade on display in store"
                    />
                    <figcaption>BBC Morning Live</figcaption>
                  </figure>

                  <p>Adidas, which equip the British Paralympic team, also do not sell single shoes online. However they say it is possible to buy a single shoe in some stores, depending on stock. The company added it is in the "advanced stages" of finalising a comprehensive policy.</p>

                  <p>Puma meanwhile, does not sell any single shoes in store or online and, like Decathlon, did not respond to a request for comment.</p>

                  <p>While Nike and Adidas have made positive steps towards addressing the issue, Stef thinks sports brands should sell single shoes at all their shops and online as standard practice.</p>

                  <h2>Small steps</h2>

                  <p>Some smaller businesses and grassroots organisations are already proving it is possible.</p>

                  <p>High street footwear company Schuh sells single shoes for half the price of a pair, while shoe chain Office also allows you to buy odd sized pairs of shoes through their outlet site.</p>

                  <p>And then there's small scale solutions such as Jo's Odd Shoes, founded by Jo O'Callaghan who lost her right leg to complex regional pain syndrome.</p>

                  <p>The condition makes it too painful to wear a prosthetic limb, leaving her, like many amputees, only needing one of a pair of shoes.</p>

                  <p>She set up a Facebook Group where members could swap or donate spare shoes. Many retailers also donate shoes to the scheme.</p>

                  <p>The items are free for members, aside from a small fee for postage and packaging.</p>

                  <figure>
                    <img
                      srcset="https://ichef.bbci.co.uk/news/240/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp 240w,
                              https://ichef.bbci.co.uk/news/320/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp 320w,
                              https://ichef.bbci.co.uk/news/480/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp 480w,
                              https://ichef.bbci.co.uk/news/640/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp 640w,
                              https://ichef.bbci.co.uk/news/800/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp 800w,
                              https://ichef.bbci.co.uk/news/1024/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp 1024w,
                              https://ichef.bbci.co.uk/news/1536/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp 1536w"
                      src="https://ichef.bbci.co.uk/news/480/cpsprodpb/9be3/live/45ebcf90-9adc-11f0-8bf4-fbf29892f319.png.webp"
                      alt="BBC Morning Live Jo on her sofa dealing with online orders via her phone. She is white, has light purple hair and her missing limb is clearly visible"
                    />
                    <figcaption>BBC Morning Live</figcaption>
                  </figure>

                  <p>It's a service that air sport athlete Jack Pimblett has benefited from.</p>

                  <p>Born with talipes, or club foot, which stunted the growth of his right foot and leg, he struggled to find two shoes which fitted well when he was younger.</p>

                  <p>He often had to put cotton wool in his shoes to fill the extra space.</p>

                  <p>As an adult, he manages by buying a combination of junior and adult shoe sizes - buying a right shoe in size 5 and a left shoe in size 7.</p>

                  <p>But this imposes a significant financial strain, with Jack typically spending around £150 for two pairs of shoes.</p>

                  <p>"It would be nice to be able to buy shoes that fit… [without] paying twice," he says.</p>

                  <p>Stef acknowledges the change she's calling for takes time and money, but urges firms to push forward.</p>

                  <p>"All it takes is that first step forward in the right direction," she says.</p>

                  <figure>
                    <img
                      srcset="https://ichef.bbci.co.uk/news/240/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp 240w,
                              https://ichef.bbci.co.uk/news/320/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp 320w,
                              https://ichef.bbci.co.uk/news/480/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp 480w,
                              https://ichef.bbci.co.uk/news/640/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp 640w,
                              https://ichef.bbci.co.uk/news/800/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp 800w,
                              https://ichef.bbci.co.uk/news/1024/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp 1024w,
                              https://ichef.bbci.co.uk/news/1536/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp 1536w"
                      src="https://ichef.bbci.co.uk/news/480/cpsprodpb/0321/live/e9f798f0-9d14-11f0-928c-71dbb8619e94.png.webp"
                      alt="Promotional banner for the Upbeat newsletter"
                    />
                  </figure>

                  <p>(Newsletter sign-up removed in this clean view.)</p>
                </article>
              </div>
            </body>
            </html>
        """.trimIndent()
        // 使用 extension function 转换为标准 HTML
        val htmlContent = remember { jsonHtml.fromJsonHtml().wrapHtml() }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HtmlText(
                html = htmlContent,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}