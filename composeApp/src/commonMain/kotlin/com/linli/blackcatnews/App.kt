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
<div data-component=\"headline-block\" class=\"sc-3b6b161a-0 bPbmDW\"><h1 class=\"sc-f98b1ad2-0 jRDKjj\">Billionaire populist eyes power in Czech vote but may need help from extremes</h1></div><div data-component=\"byline-block\" class=\"sc-3b6b161a-0 jdlrvG\"><div data-testid=\"byline-new\" class=\"sc-c4820dd1-0 cYDsPh\"><div class=\"sc-c4820dd1-1 jZKXfd\"><time datetime=\"2025-10-02T03:09:54.045Z\" class=\"sc-c4820dd1-2 IVdPK\">17 minutes ago</time><div class=\"sc-c4820dd1-3 enqVZU\"></div></div><span data-testid=\"byline-new-contributors\" class=\"sc-c4820dd1-11 bxdaHR\"><span class=\"sc-c4820dd1-7 dbQBRo\">Rob Cameron</span><span data-testid=\"undefined-role-location\" class=\"sc-c4820dd1-8 evcqcz\">Prague Correspondent, Prague</span></span></div><div class=\"sc-c4820dd1-4 dlQyxQ\"></div></div><figure><div data-component=\"image-block\" class=\"sc-3b6b161a-0 kSquvq\"><div data-testid=\"hero-image\" class=\"sc-5340b511-1 ilsLtA\"><img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/abd5/live/dd9eca10-9ed1-11f0-bb60-7394d1f81bcb.jpg.webp\" alt=\"Reuters Andrej Babis stands in front of a map of the US holding a red cap saying Silné Česko meaning 'Strong Czechia'\" class=\"sc-5340b511-0 bGQwLJ\" /><span class=\"sc-5340b511-2 lha-dta\">Reuters</span></div></div><div data-component=\"caption-block\" class=\"sc-3b6b161a-0 etSgSK\"></div></figure><div data-component=\"text-block\" class=\"sc-3b6b161a-0 jdlrvG\"><p class=\"sc-9a00e533-0 bJoRPJ\">Czechs go to the polls on Friday and Saturday facing a deteriorating security situation in Europe and fears of Russian interference.</p><p class=\"sc-9a00e533-0 bJoRPJ\">Populist billionaire Andrej Babis, 71, is tipped to head the next government, replacing the strongly pro-Western, pro-Ukraine coalition.</p><p class=\"sc-9a00e533-0 bJoRPJ\">But he'll likely need allies on the extremes of Czech politics – and their price will not be cheap.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"We'll never drag the Czech Republic to the East. I can absolutely rule that out,\" Babis told a crowd of mostly elderly supporters, gathered around a stage in the former steel town of Kladno, just outside Prague.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"We weren't the ones who sat down with Putin - we were the ones who expelled Russian diplomats!\" he went on, referring to measures taken in his first term following revelations that Russia's GRU military intelligence had blown up a Czech ammunition dump in 2014.</p></div><div data-testid=\"ad-unit\" data-component=\"ad-slot\" class=\"sc-1b734f04-0 clMRbk\"><div data-testid=\"dotcom-mid_1\" id=\"dotcom-mid_1\" class=\"dotcom-ad\"></div></div><figure><div data-component=\"image-block\" class=\"sc-3b6b161a-0 jHorPx\"><div data-testid=\"image\" class=\"sc-5340b511-1 ilsLtA\"><img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/d4cb/live/52c2b190-9ed1-11f0-84a3-990eaf5163c8.jpg.webp\" alt=\"A crowd of people surrounding a set of marquees, many of whom are wearing the Silné Česko/Strong Czechia red hats including a blonde woman in the foreground. The attendants are larger older people.\" class=\"sc-5340b511-0 bGQwLJ\" /></div></div><div data-component=\"caption-block\" class=\"sc-3b6b161a-0 etSgSK\"></div></figure><div data-component=\"text-block\" class=\"sc-3b6b161a-0 jdlrvG\"><p class=\"sc-9a00e533-0 bJoRPJ\">A row of grey heads nodded as the former prime minister warmed to his theme.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"And never - I repeat, never - will we consider leaving the European Union. Look at what happened to Great Britain! And they're a nuclear power. They've got gas, oil, a fishing industry. They're friends with Trump,\" he added.</p><p class=\"sc-9a00e533-0 bJoRPJ\">Andrej Babis himself is also friends with US President Donald Trump.</p><p class=\"sc-9a00e533-0 bJoRPJ\">Many supporters were wearing Babis's red baseball cap emblazoned with the words \"Strong Czechia\" – heavily inspired by Trump's Maga movement.</p><p class=\"sc-9a00e533-0 bJoRPJ\">The problem for Babis – and it could soon become a problem for Nato and the EU – is that his ANO party is unlikely to win an overall majority. </p><p class=\"sc-9a00e533-0 bJoRPJ\">Instead, he will likely need to form alliances with smaller parties on the fringes of Czech politics.</p><p class=\"sc-9a00e533-0 bJoRPJ\">Opinion polls and public statements suggest his choice of potential allies will be limited to the ultra-nationalist SPD, the anti-Green-Deal Motorists, and Enough! - who are an ad-hoc coalition of rebranded Communists, the remnants of the once-mighty Social Democrats, and a blogger in a black hat who calls himself \"Pitchfork\" (the Czech word for redneck).</p><p class=\"sc-9a00e533-0 bJoRPJ\">On Wednesday night, during his only head-to-head debate with Prime Minister  Petr Fiala, Andrej Babis ruled out forming a coalition with the Communists. \"I'll sign a piece of paper to that effect right here in the studio,\" he said.</p><p class=\"sc-9a00e533-0 bJoRPJ\">Several of his potential allies want referendums on leaving the EU and Nato.</p><p class=\"sc-9a00e533-0 bJoRPJ\">ANO says that is not going to happen.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"We criticise the European Union, but we don't want to destroy it, we want to reform it,\" deputy leader Karel Havlicek told the BBC.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"And Nato, well, we can criticise many things about it, but joining Nato was the most important milestone in the history of the Czech Republic, and our position is to strengthen it,\" he went on.</p></div><div data-testid=\"ad-unit\" data-component=\"ad-slot\" class=\"sc-1b734f04-0 clMRbk\"><div data-testid=\"dotcom-mid_2\" id=\"dotcom-mid_2\" class=\"dotcom-ad\"></div></div><figure><div data-component=\"image-block\" class=\"sc-3b6b161a-0 jHorPx\"><div data-testid=\"image\" class=\"sc-5340b511-1 ilsLtA\"><img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/dc98/live/42d0f050-9ed4-11f0-bb60-7394d1f81bcb.jpg.webp\" alt=\"Ondrej Kapralek is a young man with shoulder length dark hair. He is wearing glasses with a black frame and a stripy t-shirt and blazer. He is standing in a pedestrian area with shops on either side.\" class=\"sc-5340b511-0 bGQwLJ\" /></div></div><div data-component=\"caption-block\" class=\"sc-3b6b161a-0 etSgSK\"></div></figure><div data-component=\"text-block\" class=\"sc-3b6b161a-0 jdlrvG\"><p class=\"sc-9a00e533-0 bJoRPJ\">As we spoke a large inflatable dinosaur loomed over our heads, part of a bouncy castle erected for the ANO rally. For older supporters there were tents offering cups of coffee, free blood pressure checks and the red baseball caps.</p><p class=\"sc-9a00e533-0 bJoRPJ\">By the time I left they were gone.</p><p class=\"sc-9a00e533-0 bJoRPJ\">Across town, 19-year-old political science student Ondrej Kapralek, who is an activist for the liberal Pirate Party, is preparing to vote in his first election.</p><p class=\"sc-9a00e533-0 bJoRPJ\">The Pirates were part of the centre-right government until they left the coalition over a bungled digitalisation scheme.</p><p class=\"sc-9a00e533-0 bJoRPJ\">But their political star is rising again, and could attract voters disillusioned with both the government and the populist or extremist opposition.</p></div><div data-testid=\"ad-unit\" data-component=\"ad-slot\" class=\"sc-1b734f04-0 clMRbk\"><div data-testid=\"dotcom-mid_3\" id=\"dotcom-mid_3\" class=\"dotcom-ad\"></div></div><figure><div data-component=\"image-block\" class=\"sc-3b6b161a-0 jHorPx\"><div data-testid=\"image\" class=\"sc-5340b511-1 ilsLtA\"><img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/4720/live/e318be20-9eee-11f0-ab7d-dd1b4c246026.jpg.webp\" alt=\"MICHAL CIZEK/AFP A man wearing a dark jacket and striped tie points his right hand to the audience with a green Spolu coalition logo behind his hand.\" class=\"sc-5340b511-0 bGQwLJ\" /><span class=\"sc-5340b511-2 lha-dta\">MICHAL CIZEK/AFP</span></div></div><div data-component=\"caption-block\" class=\"sc-3b6b161a-0 etSgSK\"></div></figure><div data-component=\"text-block\" class=\"sc-3b6b161a-0 jdlrvG\"><p class=\"sc-9a00e533-0 bJoRPJ\">\"I certainly feel we should invest in our security,\" the student said.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"It's not just about housing, it's not just about the economy, it's not just about the EU - all of these things have to come together for my country to show my generation that they can have a great future here,\" he told the BBC.</p><p class=\"sc-9a00e533-0 bJoRPJ\">Like many young Czech progressives, he is worried his country could follow in the footsteps of Robert Fico's Slovakia or Viktor Orban's Hungary – both EU and Nato members, but increasingly illiberal and in favour of closer ties with Moscow.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"Russia is waging a massive campaign of disinformation against the Czech Republic,\" said security analyst Roman Maca, adding that Russian intelligence services were also believed to be behind cyber-attacks and cases of arson.</p><p class=\"sc-9a00e533-0 bJoRPJ\">He believes the presence of Russia-friendly parties in an ANO-led government should set alarm bells ringing across Europe.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"These parties are pro-Russian, so they will want what is good for the Kremlin,\" he told the BBC.</p><p class=\"sc-9a00e533-0 bJoRPJ\">\"They will ask ANO to stop supporting Ukraine, to end the Czech ammunition initiative, to oppose sanctions against Russia.\"</p><p class=\"sc-9a00e533-0 bJoRPJ\">ANO has already said it will scrap the initiative, which has supplied 3.5 million artillery shells to Ukraine. They want to replace it with a more transparent scheme operating within the confines of Nato.</p><p class=\"sc-9a00e533-0 bJoRPJ\">But the party's potential coalition partners want to go further, with radical cuts to defence spending and the expulsion of Ukrainian refugees.</p><p class=\"sc-9a00e533-0 bJoRPJ\">For the past few years, the Czech government's loyalty - to the EU, to Nato, to the defence of Ukraine – has been unwavering.</p><p class=\"sc-9a00e533-0 bJoRPJ\">If the opinion polls are accurate, that approach will soon – at the very least – be called into question.</p></div><div data-component=\"tags\" class=\"sc-3b6b161a-0 cnKFtO\"><div class=\"sc-6bf80075-0 eArXfh\"><div data-testid=\"anchor-inner-wrapper\"><a href=\"https://www.bbc.com/news/topics/cewrdwy69g4t\" data-testid=\"internal-link\" class=\"sc-57521d02-0 ePxXNS\">Prague</a></div><div data-testid=\"anchor-inner-wrapper\"><a href=\"https://www.bbc.com/news/topics/cvenzmgyg1lt\" data-testid=\"internal-link\" class=\"sc-57521d02-0 ePxXNS\">Czech Republic</a></div></div></div>

""".trimIndent()
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