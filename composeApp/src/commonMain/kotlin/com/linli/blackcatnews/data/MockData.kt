object MockData {
    val mockResponseNormal: String = """
        {
          "message": "success",
          "requestedCount": 1,
          "actualCount": 1,
          "totalAvailable": 1,
          "processedCount": 1,
          "filters": {
            "source": "BBC",
            "section": "news",
            "difficulties": ["NORMAL"],
            "from_language": "en",
            "to_language": "zh-TW"
          },
          "articles": [
            {
              "id": 14,
          "sourceName": "BBC",
          "section": "news",
          "title": "Isas, cars and pensions: How the Budget affects you",
          "publishedAt": "2025-11-26T13:57:21Z",
          "originalUrl": "https://www.bbc.com/news/articles/c5y2g2qn0eyo?at_medium=RSS&at_campaign=rss",
          "contentHtml": "<article>\n  <h1>Isas, cars and pensions: How the Budget affects you</h1>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp\" alt=\"Mother carrying her young daughter while unloading groceries into their white electric car in a supermarket\" />\n    <figcaption>Getty Images</figcaption>\n  </figure>\n\n  <p>Chancellor Rachel Reeves has announced her Budget, although the official forecaster published some details early. This summary highlights the main measures and explains how they may affect household finances. It covers taxes, transport, savings, pensions and benefits so you can see what might change for you.</p>\n\n  <h2>You may pay more tax</h2>\n  <p>The income levels at which different income tax rates apply will remain frozen until 2031. This means that pay rises could push people into higher tax bands or make a larger share of income liable to tax. Scotland sets its own income tax rates, and VAT on goods and services remains unchanged, which can affect lower earners more.</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp\" alt=\"Table showing income tax levels in England, Wales, and Northern Ireland\" />\n    <figcaption>Income tax levels in England, Wales and Northern Ireland</figcaption>\n  </figure>\n\n  <h2>Driving an EV will cost more</h2>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp\" alt=\"A white Tesla Electric EV car charges at a Tesla Public Supercharger network\" />\n    <figcaption>John Keeble/Getty Images</figcaption>\n  </figure>\n  <p>From 2028, electric and plug-in hybrid vehicles will face new road pricing. The plan charges EV drivers 3p per mile and plug-in hybrids 1.5p per mile, in addition to existing road taxes, and rates will rise with inflation. The temporary 5p cut in fuel duty on petrol and diesel will be extended from April but will be increased in stages from September 2026.</p>\n  <p>Regulated rail fares in England will be frozen until March 2027, affecting many season tickets and commuter journeys, while unregulated fares remain at operators' discretion. The existing bus fare cap of £3 for single journeys in most of England is already set until March 2027.</p>\n\n  <h2>Cash Isa limits, pensions and housing changes</h2>\n  <p>The annual allowance for cash Individual Savings Accounts will fall from £20,000 to £12,000 for under-65s, while those aged 65 and over keep a £20,000 limit. The government hopes this will encourage more investment in stocks and shares Isas, but that shift carries greater risk.</p>\n  <p>From April 2027, rates of tax on savings income will rise by two percentage points, making basic, higher and additional rates 22%, 42% and 47% respectively. Pension salary sacrifice arrangements will face a £2,000-a-year cap from April 2029, although contributions above that can still be made and will be taxed.</p>\n  <p>Homeowners in England with properties valued at £2m or more will face a council tax surcharge from April 2028, with bands rising to as much as £7,500 for the most expensive homes. Valuation of the top council tax bands will be required for the first time since 1991.</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp\" alt=\"Table showing council tax bands in England\" />\n    <figcaption>Council tax bands in England (based on 1991 values)</figcaption>\n  </figure>\n\n  <h2>Families, benefits and other measures</h2>\n  <p>The two-child limit for universal credit and tax credits will be removed from April next year, which means many families with three or more children will receive more support. Some benefits, including main disability benefits and carer's allowance, will rise by 3.8% in April to reflect prices.</p>\n  <p>The state pension will increase by 4.8% in April in line with average wages, raising the new flat-rate state pension to £241.30 a week. The NHS prescription charge in England will be frozen at £9.90, and a tax on sugary drinks will be extended in 2028 to cover some milk-based products.</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp\" alt=\"Table showing the amount of sugar in milk-based drinks\" />\n    <figcaption>Sugar content in some milk-based drinks</figcaption>\n  </figure>\n\n  <p>Other measures include restrictions on high-end Motability vehicles and possible powers for mayors to introduce a levy on overnight stays. Landlord tax changes may lead forecasters to expect higher rents, and plans include replacing the Lifetime Isa to focus more on saving for first homes.</p>\n</article>",
          "titleFrom": "Isas, cars and pensions: How the Budget affects you",
          "titleTo": "ISA、車輛與退休金：預算案如何影響你",
          "title_zh": "ISA、車輛與退休金：預算案如何影響你",
          "summaryFrom": "- The Chancellor announced a Budget covering taxes, transport, savings, pensions and benefits, with some details published early by the forecaster.\n- Income tax thresholds are frozen until 2031, which may push people into higher tax bands if salaries rise.\n- New road pricing for electric and plug-in hybrid vehicles will start in 2028; fuel duty cuts will be temporary and then rise in stages.\n- Cash ISA annual allowance falls from £20,000 to £12,000 for under-65s; savings tax rates rise from April 2027; pension salary-sacrifice will face a £2,000 cap from 2029.\n- Some benefits will increase and the two-child limit for universal credit and tax credits will be removed; the state pension and some charges/levies are adjusted.",
          "summaryTo": "- 財政部長公布了涵蓋稅務、運輸、儲蓄、退休金與福利的預算案，部分細節由官方預測機構提前公布。\n- 所得稅級距凍結至2031年，若薪資上漲可能導致更多人進入較高稅級或更多收入需課稅。\n- 電動車與插電式混合車自2028年起將面臨新路徑收費；汽油稅的5便士暫時減免會延長，但之後分階段上調。\n- 現金型ISA年度額度對65歲以下從£20,000降到£12,000；儲蓄所得稅自2027年4月起提高；薪資交換（salary sacrifice）設每年£2,000上限自2029年起。\n- 部分福利將上調，普遍信貸與稅額抵減的「兩孩上限」將移除；國家退休金與某些收費/徵費也有調整。",
          "summary_en": "- The Chancellor announced a Budget covering taxes, transport, savings, pensions and benefits, with some details published early by the forecaster.\n- Income tax thresholds are frozen until 2031, which may push people into higher tax bands if salaries rise.\n- New road pricing for electric and plug-in hybrid vehicles will start in 2028; fuel duty cuts will be temporary and then rise in stages.\n- Cash ISA annual allowance falls from £20,000 to £12,000 for under-65s; savings tax rates rise from April 2027; pension salary-sacrifice will face a £2,000 cap from 2029.\n- Some benefits will increase and the two-child limit for universal credit and tax credits will be removed; the state pension and some charges/levies are adjusted.",
          "summary_zh": "- 財政部長公布了涵蓋稅務、運輸、儲蓄、退休金與福利的預算案，部分細節由官方預測機構提前公布。\n- 所得稅級距凍結至2031年，若薪資上漲可能導致更多人進入較高稅級或更多收入需課稅。\n- 電動車與插電式混合車自2028年起將面臨新路徑收費；汽油稅的5便士暫時減免會延長，但之後分階段上調。\n- 現金型ISA年度額度對65歲以下從£20,000降到£12,000；儲蓄所得稅自2027年4月起提高；薪資交換（salary sacrifice）設每年£2,000上限自2029年起。\n- 部分福利將上調，普遍信貸與稅額抵減的「兩孩上限」將移除；國家退休金與某些收費/徵費也有調整。",
          "requested_difficulties": [
            "NORMAL"
          ],
          "from_language": "en",
          "to_language": "zh-TW",
          "content": {
            "difficulty": "NORMAL",
            "from_language": "en",
            "to_language": "zh-TW",
            "optimized_html_from": "<article>\n  <h1>Isas, cars and pensions: How the Budget affects you</h1>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp\" alt=\"Mother carrying her young daughter while unloading groceries into their white electric car in a supermarket\" />\n    <figcaption>Getty Images</figcaption>\n  </figure>\n\n  <p>Chancellor Rachel Reeves has announced her Budget, although the official forecaster published some details early. This summary highlights the main measures and explains how they may affect household finances. It covers taxes, transport, savings, pensions and benefits so you can see what might change for you.</p>\n\n  <h2>You may pay more tax</h2>\n  <p>The income levels at which different income tax rates apply will remain frozen until 2031. This means that pay rises could push people into higher tax bands or make a larger share of income liable to tax. Scotland sets its own income tax rates, and VAT on goods and services remains unchanged, which can affect lower earners more.</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp\" alt=\"Table showing income tax levels in England, Wales, and Northern Ireland\" />\n    <figcaption>Income tax levels in England, Wales and Northern Ireland</figcaption>\n  </figure>\n\n  <h2>Driving an EV will cost more</h2>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp\" alt=\"A white Tesla Electric EV car charges at a Tesla Public Supercharger network\" />\n    <figcaption>John Keeble/Getty Images</figcaption>\n  </figure>\n  <p>From 2028, electric and plug-in hybrid vehicles will face new road pricing. The plan charges EV drivers 3p per mile and plug-in hybrids 1.5p per mile, in addition to existing road taxes, and rates will rise with inflation. The temporary 5p cut in fuel duty on petrol and diesel will be extended from April but will be increased in stages from September 2026.</p>\n  <p>Regulated rail fares in England will be frozen until March 2027, affecting many season tickets and commuter journeys, while unregulated fares remain at operators' discretion. The existing bus fare cap of £3 for single journeys in most of England is already set until March 2027.</p>\n\n  <h2>Cash Isa limits, pensions and housing changes</h2>\n  <p>The annual allowance for cash Individual Savings Accounts will fall from £20,000 to £12,000 for under-65s, while those aged 65 and over keep a £20,000 limit. The government hopes this will encourage more investment in stocks and shares Isas, but that shift carries greater risk.</p>\n  <p>From April 2027, rates of tax on savings income will rise by two percentage points, making basic, higher and additional rates 22%, 42% and 47% respectively. Pension salary sacrifice arrangements will face a £2,000-a-year cap from April 2029, although contributions above that can still be made and will be taxed.</p>\n  <p>Homeowners in England with properties valued at £2m or more will face a council tax surcharge from April 2028, with bands rising to as much as £7,500 for the most expensive homes. Valuation of the top council tax bands will be required for the first time since 1991.</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp\" alt=\"Table showing council tax bands in England\" />\n    <figcaption>Council tax bands in England (based on 1991 values)</figcaption>\n  </figure>\n\n  <h2>Families, benefits and other measures</h2>\n  <p>The two-child limit for universal credit and tax credits will be removed from April next year, which means many families with three or more children will receive more support. Some benefits, including main disability benefits and carer's allowance, will rise by 3.8% in April to reflect prices.</p>\n  <p>The state pension will increase by 4.8% in April in line with average wages, raising the new flat-rate state pension to £241.30 a week. The NHS prescription charge in England will be frozen at £9.90, and a tax on sugary drinks will be extended in 2028 to cover some milk-based products.</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp\" alt=\"Table showing the amount of sugar in milk-based drinks\" />\n    <figcaption>Sugar content in some milk-based drinks</figcaption>\n  </figure>\n\n  <p>Other measures include restrictions on high-end Motability vehicles and possible powers for mayors to introduce a levy on overnight stays. Landlord tax changes may lead forecasters to expect higher rents, and plans include replacing the Lifetime Isa to focus more on saving for first homes.</p>\n</article>",
            "optimized_html_to": "<article>\n  <h1>Isas、汽車與退休金：預算如何影響你</h1>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp\" alt=\"母親抱著幼女，在超市把雜貨放入白色電動車的後車廂\" />\n    <figcaption>Getty Images</figcaption>\n  </figure>\n\n  <p>財政大臣瑞秋·里夫斯已經公布預算案，但官方預測機構提前發佈了一些細節。本文摘要主要措施，說明這些改變可能如何影響家庭的財務狀況。文章涵蓋稅制、交通、儲蓄、退休金與福利，讓你了解哪些方面會改變。</p>\n\n  <h2>你可能要繳更多稅</h2>\n  <p>不同所得稅率適用的收入門檻將凍結至2031年。這表示加薪可能把人推入更高的稅級，或使收入中被課稅的比重增加。蘇格蘭有自己的所得稅率，而商品與服務的增值稅維持不變，對低收入者影響更大。</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp\" alt=\"顯示英格蘭、威爾斯與北愛爾蘭所得稅級距的表格\" />\n    <figcaption>英格蘭、威爾斯與北愛爾蘭的所得稅級距</figcaption>\n  </figure>\n\n  <h2>電動車開銷將增加</h2>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp\" alt=\"在特斯拉公共充電站充電的白色電動車\" />\n    <figcaption>John Keeble/Getty Images</figcaption>\n  </figure>\n  <p>從2028年起，電動車與插電式混合動力車將開始收取新路費。電動車每英里收取3便士，插電混合動力車收取1.5便士，這些費用會加到現有的道路稅之外，並會隨通膨上調。汽油與柴油的臨時5便士燃油稅減免將自四月延長，但從2026年9月起會分階段回升。</p>\n  <p>英格蘭受管制的鐵路票價將凍結到2027年3月，這包括多數通勤季票，而營運商仍可自行定價未受管制的票種。英格蘭大部分路線的單程巴士票上限為3英鎊，該上限也維持至2027年3月。</p>\n\n  <h2>現金ISA、退休金與房屋變動</h2>\n  <p>現金個人儲蓄帳戶的免稅年度上限，對65歲以下者將由2萬英鎊降至1.2萬英鎊；65歲及以上者仍可保留2萬英鎊的上限。政府希望此舉能鼓勵更多人轉向股票與基金等投資型ISA，但那類選擇風險較高。</p>\n  <p>自2027年4月起，儲蓄所得稅各級稅率將上調兩個百分點，使基本、較高與附加稅率分別達到22%、42%與47%。透過薪資犧牲方式進入退休金的金額，從2029年4月起將設年上限2,000英鎊，超過部分仍可供款但會被課稅。</p>\n  <p>英格蘭價值兩百萬英鎊或以上的住宅，將從2028年4月起面臨市議會稅的附加費，最高級距的附加費可達7,500英鎊。自1991年以來，最高稅級的房產將首次需要重新估值。</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp\" alt=\"顯示英格蘭房產市議會稅分級的表格\" />\n    <figcaption>英格蘭的市議會稅分級（基於1991年值）</figcaption>\n  </figure>\n\n  <h2>家庭、福利與其他措施</h2>\n  <p>兩孩限制（two-child cap）將於明年四月取消，這使得有三名或以上孩子的父母在普遍信貸與稅收福利方面可獲得更多支持。部分福利，包括主要的殘障津貼與照顧者津貼，將於四月上調3.8%，以反映物價上升。</p>\n  <p>國家退休金將按平均工資上漲4.8%，新的平等年金將升至每週241.30英鎊。英格蘭的NHS處方費將在四月再次凍結於9.90英鎊，含糖飲料稅也會在2028年擴展到某些高糖牛奶飲品。</p>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp\" alt=\"顯示某些牛奶基飲品中的糖含量比較表\" />\n    <figcaption>某些牛奶基飲品的含糖量比較</figcaption>\n  </figure>\n\n  <p>其他措施包括限制Motability計畫中某些高級品牌車款，以及可能授權市長對過夜住宿徵收稅費。房東課稅增加，預計可能導致租金上升；此外，Lifetime Isa也計畫改為更專注於購首套房的儲蓄安排。</p>\n</article>",
            "cleaned_text_from": "Chancellor Rachel Reeves has announced her Budget, although the official forecaster published some details early. This summary highlights the main measures and explains how they may affect household finances. It covers taxes, transport, savings, pensions and benefits so you can see what might change for you.\n\nThe income levels at which different income tax rates apply will remain frozen until 2031. This means that pay rises could push people into higher tax bands or make a larger share of income liable to tax. Scotland sets its own income tax rates, and VAT on goods and services remains unchanged, which can affect lower earners more.\n\nFrom 2028, electric and plug-in hybrid vehicles will face new road pricing. The plan charges EV drivers 3p per mile and plug-in hybrids 1.5p per mile, in addition to existing road taxes, and rates will rise with inflation. The temporary 5p cut in fuel duty on petrol and diesel will be extended from April but will be increased in stages from September 2026. Regulated rail fares in England will be frozen until March 2027, affecting many season tickets and commuter journeys, while unregulated fares remain at operators' discretion.\n\nThe annual allowance for cash Individual Savings Accounts will fall from £20,000 to £12,000 for under-65s, while those aged 65 and over keep a £20,000 limit. From April 2027, rates of tax on savings income will rise by two percentage points, making basic, higher and additional rates 22%, 42% and 47% respectively. Pension salary sacrifice arrangements will face a £2,000-a-year cap from April 2029, although contributions above that can still be made and will be taxed. Homeowners in England with properties valued at £2m or more will face a council tax surcharge from April 2028, with bands rising to as much as £7,500 for the most expensive homes.\n\nThe two-child limit for universal credit and tax credits will be removed from April next year, which means many families with three or more children will receive more support. Some benefits, including main disability benefits and carer's allowance, will rise by 3.8% in April to reflect prices. The state pension will increase by 4.8% in April in line with average wages, raising the new flat-rate state pension to £241.30 a week. The NHS prescription charge in England will be frozen at £9.90, and a tax on sugary drinks will be extended in 2028 to cover some milk-based products. Other changes include restrictions on high-end Motability vehicles and possible powers for mayors to introduce a levy on overnight stays; landlord tax changes may lead forecasters to expect higher rents and the Lifetime Isa will be refocused to help first-time buyers.",
            "cleaned_text_to": "財政大臣瑞秋·里夫斯已經公布預算案，但官方預測機構提前發佈了一些細節。本文摘要主要措施，說明這些改變可能如何影響家庭的財務狀況。文章涵蓋稅制、交通、儲蓄、退休金與福利，讓你了解哪些方面會改變。\n\n不同所得稅率適用的收入門檻將凍結至2031年。這表示加薪可能把人推入更高的稅級，或使收入中被課稅的比重增加。蘇格蘭有自己的所得稅率，而商品與服務的增值稅維持不變，對低收入者影響更大。\n\n從2028年起，電動車與插電式混合動力車將開始收取新路費。電動車每英里收取3便士，插電混合動力車收取1.5便士，這些費用會加到現有的道路稅之外，並會隨通膨上調。汽油與柴油的臨時5便士燃油稅減免將自四月延長，但從2026年9月起會分階段回升。英格蘭受管制的鐵路票價將凍結到2027年3月，這包括多數通勤季票，而營運商仍可自行定價未受管制的票種。\n\n現金個人儲蓄帳戶的免稅年度上限，對65歲以下者將由2萬英鎊降至1.2萬英鎊；65歲及以上者仍可保留2萬英鎊的上限。自2027年4月起，儲蓄所得稅各級稅率將上調兩個百分點，使基本、較高與附加稅率分別達到22%、42%與47%。透過薪資犧牲方式進入退休金的金額，從2029年4月起將設年上限2,000英鎊，超過部分仍可供款但會被課稅。英格蘭價值兩百萬英鎊或以上的住宅，將從2028年4月起面臨市議會稅的附加費，最高級距的附加費可達7,500英鎊。\n\n兩孩限制將於明年四月取消，這使得有三名或以上孩子的父母在普遍信貸與稅收福利方面可獲得更多支持。部分福利，包括主要的殘障津貼與照顧者津貼，將於四月上調3.8%，以反映物價上升。國家退休金將按平均工資上漲4.8%，新的平等年金將升至每週241.30英鎊。英格蘭的NHS處方費將在四月再次凍結於9.90英鎊，含糖飲料稅也會在2028年擴展到某些高糖牛奶飲品。其他措施包括限制Motability計畫中某些高級品牌車款，可能授權市長對過夜住宿徵稅，以及房東課稅增加導致租金上升的風險；Lifetime Isa也將改為更專注於購首套房的儲蓄安排。",
            "explanation": {
              "vocabulary": [
                {
                  "pos": "verb/adjective",
                  "word_from": "freeze / frozen",
                  "word_to": "凍結 / 被凍結",
                  "definition_from": "To stop something from changing; 'frozen' describes a state that is prevented from being altered.",
                  "definition_to": "停止變動；「被凍結」表示某項目被禁止改變或維持原狀。",
                  "example_from": "The income tax thresholds will remain frozen until 2031, meaning they won’t change despite inflation.",
                  "example_to": "所得稅級距會被凍結到2031年，意即即使有通膨也不會調整。"
                },
                {
                  "pos": "noun",
                  "word_from": "surcharge",
                  "word_to": "附加費／額外徵收",
                  "definition_from": "An extra charge or payment added on top of the usual cost.",
                  "definition_to": "在原本費用上額外增加的收費。",
                  "example_from": "Homeowners with expensive properties will face a council tax surcharge from April 2028.",
                  "example_to": "高價值房屋的屋主自2028年4月起將面臨市政稅附加費。"
                },
                {
                  "pos": "noun",
                  "word_from": "allowance",
                  "word_to": "額度／免稅額",
                  "definition_from": "An amount of money permitted or not taxed; e.g. an annual allowance for savings accounts.",
                  "definition_to": "允許的金額或免稅額，例如儲蓄帳戶的年度額度。",
                  "example_from": "The annual allowance for cash ISAs will fall from £20,000 to £12,000 for under-65s.",
                  "example_to": "現金型ISA的年度額度對65歲以下將從£20,000降為£12,000。"
                },
                {
                  "pos": "verb",
                  "word_from": "refocus",
                  "word_to": "重新聚焦／改以…為重心",
                  "definition_from": "To change the main purpose or emphasis of something.",
                  "definition_to": "改變主要目的或重點。",
                  "example_from": "The Lifetime ISA will be refocused to help first-time buyers.",
                  "example_to": "Lifetime ISA將被重新聚焦，以協助首次購屋者。"
                },
                {
                  "pos": "noun",
                  "word_from": "discretion",
                  "word_to": "酌情決定權／自由裁量",
                  "definition_from": "The power or freedom to decide what should be done in a particular situation.",
                  "definition_to": "在特定情況下決定行動的權力或自由。",
                  "example_from": "Unregulated fares remain at operators' discretion.",
                  "example_to": "未受管制的票價仍由經營者自行決定（酌情處理）。"
                },
                {
                  "pos": "noun",
                  "word_from": "cap",
                  "word_to": "上限",
                  "definition_from": "A limit or maximum amount that is allowed.",
                  "definition_to": "允許的限制或最高金額。",
                  "example_from": "Pension salary sacrifice arrangements will face a £2,000-a-year cap from April 2029.",
                  "example_to": "從2029年4月起，退休金的薪資交換安排將面臨每年£2,000的上限。"
                }
              ],
              "grammar": [
                {
                  "rule_from": "Present participle clauses (reduced clauses) to show result or cause",
                  "rule_to": "現在分詞片語（簡化從句）表示結果或原因",
                  "explanation_from": "A present participle (-ing) clause can shorten a full clause and indicate cause or result. In the article: 'making basic, higher and additional rates 22%...' shortens 'which will make...'.",
                  "explanation_to": "現在分詞（-ing）片語可以把完整子句縮短，用來表示原因或結果。文章中「making basic, higher and additional rates 22%...」就是縮短自「which will make...」。",
                  "example_from": "Full clause: This change will make tax rates higher. Reduced: This change, making tax rates higher, will affect savers.",
                  "example_to": "完整句：This change will make tax rates higher. 縮短：This change, making tax rates higher, will affect savers.（此變動使稅率提高，將影響儲蓄者。）"
                },
                {
                  "rule_from": "Modal verbs for possibility and prediction (could, will)",
                  "rule_to": "情態動詞表示可能性與預測（could、will）",
                  "explanation_from": "'Could' expresses possibility (e.g., pay rises could push people into higher tax bands). 'Will' is used for definite future events or official plans (e.g., fares will be frozen).",
                  "explanation_to": "'Could' 表示可能性（如 pay rises could push people into higher tax bands）。'Will' 用於確定的未來事件或官方計畫（如 fares will be frozen）。",
                  "example_from": "Possibility: Pay rises could cause higher tax bills. Prediction/plan: Regulated rail fares will be frozen until March 2027.",
                  "example_to": "可能性：薪資上漲可能導致更高的稅負。預測/計畫：管制火車票價將凍結到2027年3月。"
                },
                {
                  "rule_from": "Passive voice for policies and official measures",
                  "rule_to": "被動語態用於政策與官方措施",
                  "explanation_from": "News often uses passive to emphasise actions or measures rather than agents: 'will be extended', 'will face a surcharge'. This focuses on the policy impact.",
                  "explanation_to": "新聞常用被動語態強調行動或措施而非執行者：will be extended、will face a surcharge。這樣可把焦點放在政策影響上。",
                  "example_from": "Active: The government will extend the fuel duty cut. Passive: The fuel duty cut will be extended.",
                  "example_to": "主動：政府會延長油稅減免。被動：油稅減免將被延長。"
                }
              ],
              "sentence_patterns": [
                {
                  "pattern_from": "This means that + clause",
                  "pattern_to": "This means that + 子句（表示結果或結論）",
                  "explanation_from": "Used to explain the consequence or implication of a previous statement.",
                  "explanation_to": "用來說明前述陳述的結果或含意。",
                  "example_from": "The thresholds are frozen. This means that more people may pay higher taxes.",
                  "example_to": "級距被凍結。This means that 更多人可能需繳較高稅。"
                },
                {
                  "pattern_from": "From [time], [subject] will [verb] ...",
                  "pattern_to": "From [時間], [主語] will [動詞] ...（表示從某時間起的變化）",
                  "explanation_from": "A clear pattern to state when a new rule or change will start.",
                  "explanation_to": "表達某項新規則或改變從何時開始的清楚句型。",
                  "example_from": "From 2028, electric vehicles will face new road pricing.",
                  "example_to": "From 2028, 電動車將面臨新的路徑收費。"
                },
                {
                  "pattern_from": "[A] will be X, while [B] will be Y",
                  "pattern_to": "A 將會 X，而 B 將會 Y（對比或同時說明兩個情況）",
                  "explanation_from": "Use while/whereas to contrast two simultaneous situations or to compare different treatments.",
                  "explanation_to": "用 while/whereas 來對比兩個同時發生的情況或不同處理方式。",
                  "example_from": "Regulated fares will be frozen until March 2027, while unregulated fares remain at operators' discretion.",
                  "example_to": "管制票價會凍結到2027年3月，而未受管制的票價仍由經營者酌情決定。"
                }
              ],
              "phrases_idioms": [
                {
                  "phrase_from": "push into",
                  "phrase_to": "推入／導致進入",
                  "explanation_from": "To cause someone or something to move into a different state or situation (often negative).",
                  "explanation_to": "使人或物進入另一種狀態或情況（通常帶負面）。",
                  "example_from": "Pay rises could push people into higher tax bands.",
                  "example_to": "薪資上漲可能會把人推入較高的稅級。"
                },
                {
                  "phrase_from": "face a surcharge",
                  "phrase_to": "面臨附加費",
                  "explanation_from": "To be required to pay an extra cost or fee.",
                  "explanation_to": "需要支付額外費用或徵收。",
                  "example_from": "Homeowners with properties valued at £2m or more will face a council tax surcharge.",
                  "example_to": "價值200萬英鎊以上的房主將面臨市政稅的附加費。"
                },
                {
                  "phrase_from": "in line with",
                  "phrase_to": "依照／與…一致",
                  "explanation_from": "Matching or consistent with something, often used for policies tied to a measure (e.g., wages, inflation).",
                  "explanation_to": "與某事物相符或一致，常用於政策與某指標掛鉤（如薪資、通膨）。",
                  "example_from": "The state pension will increase by 4.8% in April in line with average wages.",
                  "example_to": "國家退休金將於4月上調4.8%，與平均薪資保持一致（掛鉤）。"
                },
                {
                  "phrase_from": "at [someone]'s discretion",
                  "phrase_to": "由某人酌情決定",
                  "explanation_from": "Left to the decision or judgment of a particular person or group.",
                  "explanation_to": "由特定人或團體判斷決定。",
                  "example_from": "Unregulated fares remain at operators' discretion.",
                  "example_to": "未受管制的票價仍由經營者自行酌情決定。"
                }
              ],
              "comprehension_mcq": [
                {
                  "question_from": "1) What is the effect of freezing income tax thresholds until 2031?",
                  "question_to": "1) 將所得稅級距凍結至2031年的影響是什麼？",
                  "options": [
                    {
                      "label": "A",
                      "text_from": "Pay rises could push people into higher tax bands.",
                      "text_to": "薪資上漲可能會使人進入較高的稅級。"
                    },
                    {
                      "label": "B",
                      "text_from": "Tax rates will be reduced across the board.",
                      "text_to": "稅率將全面降低。"
                    },
                    {
                      "label": "C",
                      "text_from": "All taxpayers will receive a one-off rebate.",
                      "text_to": "所有納稅人將獲得一次性退稅。"
                    },
                    {
                      "label": "D",
                      "text_from": "Income tax will be abolished after 2031.",
                      "text_to": "所得稅將在2031年後廢除。"
                    }
                  ],
                  "answer": "A",
                  "explanation_from": "Freezing thresholds means the bands don't change with inflation or pay rises, so rising pay can move people into higher bands — key phrase: 'remain frozen until 2031' and 'could push'.",
                  "explanation_to": "凍結級距表示級距不會隨通膨或薪資上升調整，因此薪資上漲可能把人推入較高稅級。重點片語：'remain frozen until 2031' 與 'could push'。"
                },
                {
                  "question_from": "2) How will electric vehicles be charged under the new plan from 2028?",
                  "question_to": "2) 根據2028年的新計畫，電動車將如何被收費？",
                  "options": [
                    {
                      "label": "A",
                      "text_from": "EV drivers will pay 3p per mile, plus existing road taxes.",
                      "text_to": "電動車駕駛將按英里支付3便士，外加現有道路稅。"
                    },
                    {
                      "label": "B",
                      "text_from": "All electric vehicles will be exempt from any road charges.",
                      "text_to": "所有電動車將免除任何道路收費。"
                    },
                    {
                      "label": "C",
                      "text_from": "Drivers will pay a fixed annual fee only.",
                      "text_to": "駕駛人只需支付固定的年費。"
                    },
                    {
                      "label": "D",
                      "text_from": "Only plug-in hybrids will be charged, not fully electric cars.",
                      "text_to": "只有插電式混合動力車會被收費，純電動車不會。"
                    }
                  ],
                  "answer": "A",
                  "explanation_from": "The article states EV drivers will be charged 3p per mile and plug-in hybrids 1.5p per mile in addition to existing road taxes — look for the specific per-mile rates.",
                  "explanation_to": "文章指出電動車每英里收3便士，插電式混合車每英里1.5便士，且是加在現有道路稅之上——尋找每英里的具體費率。"
                },
                {
                  "question_from": "3) Which change applies to cash ISAs for people under 65?",
                  "question_to": "3) 對於65歲以下者，現金型ISA的哪項變動適用？",
                  "options": [
                    {
                      "label": "A",
                      "text_from": "The annual allowance will fall from £20,000 to £12,000.",
                      "text_to": "年度額度將從£20,000降為£12,000。"
                    },
                    {
                      "label": "B",
                      "text_from": "The allowance will be increased to £25,000.",
                      "text_to": "額度將提高到£25,000。"
                    },
                    {
                      "label": "C",
                      "text_from": "All ISA contributions will become tax-free forever.",
                      "text_to": "所有ISA供款將永久免稅。"
                    },
                    {
                      "label": "D",
                      "text_from": "ISAs will be abolished for everyone under 65.",
                      "text_to": "ISAs 將對所有65歲以下者廢止。"
                    }
                  ],
                  "answer": "A",
                  "explanation_from": "The article clearly states the annual allowance for cash ISAs will drop to £12,000 for under-65s while those 65+ keep £20,000 — note the age distinction.",
                  "explanation_to": "文章明確指出65歲以下的現金型ISA年度額度會降到£12,000，而65歲及以上仍維持£20,000——注意年齡上的區分。"
                },
                {
                  "question_from": "4) What will happen to the two-child limit for universal credit and tax credits?",
                  "question_to": "4) 普遍信貸與稅額抵減的兩孩上限將如何處理？",
                  "options": [
                    {
                      "label": "A",
                      "text_from": "The two-child limit will be removed, increasing support for larger families.",
                      "text_to": "兩孩上限將被移除，會增加對較大家庭的支持。"
                    },
                    {
                      "label": "B",
                      "text_from": "The two-child limit will be reduced to one child only.",
                      "text_to": "兩孩上限將改為僅限一孩。"
                    },
                    {
                      "label": "C",
                      "text_from": "The limit will stay the same with no changes.",
                      "text_to": "該上限將維持不變。"
                    },
                    {
                      "label": "D",
                      "text_from": "Benefits will be cut for all families regardless of children.",
                      "text_to": "所有家庭的福利將被削減，不論子女數量。"
                    }
                  ],
                  "answer": "A",
                  "explanation_from": "The article says the two-child limit will be removed from April next year, so many families with three or more children will receive more support — key: 'removed' and 'three or more'.",
                  "explanation_to": "文章指出兩孩上限將自明年4月移除，因此有三名或以上子女的家庭會獲得更多支援——關鍵詞：'removed' 與 'three or more'。"
                }
              ]
            }
          },
          "available_difficulties": [
            "VERY_EASY",
            "NORMAL"
          ],
          "available_views": [
            {
              "from_language": "en",
              "to_language": "zh-TW",
              "difficulty": "VERY_EASY"
            },
            {
              "from_language": "en",
              "to_language": "zh-TW",
              "difficulty": "NORMAL"
            }
          ]
        }
      ]
    }
    """.trimIndent()
    val mockResponseEasy: String = """
        {
          "message": "success",
          "requestedCount": 1,
          "actualCount": 1,
          "totalAvailable": 37,
          "processedCount": 1,
          "filters": {
            "source": null,
            "section": "news",
            "difficulties": [
              "VERY_EASY"
            ],
            "from_language": "en",
            "to_language": "zh-TW"
          },
          "articles": [
            {
              "id": 14,
              "sourceName": "BBC",
              "section": "news",
              "title": "Isas, cars and pensions: How the Budget affects you",
              "publishedAt": "2025-11-26T13:57:21Z",
              "originalUrl": "https://www.bbc.com/news/articles/c5y2g2qn0eyo?at_medium=RSS&at_campaign=rss",
              "contentHtml": "<article>\n  <h1>Isas, cars and pensions: How the Budget affects you</h1>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp\" alt=\"Mother carrying her young daughter while unloading groceries into their white electric car in a supermarket\" />\n    <figcaption>Getty Images: A mother with her child and an electric car</figcaption>\n  </figure>\n\n  <p>Chancellor Rachel Reeves announced the Budget. The official forecaster published details early. This article shows the key changes and how they affect you.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp\" alt=\"Table showing income tax levels and personal allowance figures\" />\n    <figcaption>Table: Income tax bands and allowances</figcaption>\n  </figure>\n\n  <p>Tax bands will stay frozen until 2031. A pay rise can push some people into a higher tax rate. VAT and many other taxes stay the same.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp\" alt=\"A white Tesla electric car charging at a public charging station\" />\n    <figcaption>John Keeble/Getty Images: An electric car at a charging station</figcaption>\n  </figure>\n\n  <p>Electric cars will face a new road charge from 2028. Electric cars will pay 3p per mile and plug-in hybrids 1.5p per mile. The government will also keep some fuel duty changes for now and then change them later.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp\" alt=\"Table showing council tax bands in England based on property values\" />\n    <figcaption>Table: Council tax bands and values</figcaption>\n  </figure>\n\n  <p>Homes worth \u0000A32m or more will face a council tax surcharge from April 2028. The surcharge will have several price bands and affect many homes in expensive areas. The government will revalue the top council tax bands for the first time since 1991.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp\" alt=\"Table showing sugar in milk-based drinks compared with other drinks\" />\n    <figcaption>Table: Sugar content in some drinks</figcaption>\n  </figure>\n\n  <p>The cash ISA limit for under 65s will fall from \u0000A320,000 to \u0000A312,000 each year. The government wants people to invest more in other accounts. The state pension will rise and many benefits will rise in April.</p>\n\n  <p>The two-child cap on some benefits will end next April. The minimum wage will go up in April and many workers will get a pay rise. Rules on pension salary sacrifice will change and there will be a cap from April 2029.</p>\n</article>",
              "titleFrom": "Budget changes: Taxes, cars and pensions",
              "titleTo": "預算重點：稅務、電動車與退休金",
              "title_zh": "預算重點：稅務、電動車與退休金",
              "summaryFrom": "- The Chancellor announced the Budget with key changes on taxes, cars and pensions.\n- Tax bands are frozen until 2031; this may move some people into higher tax rates.\n- Electric cars will pay a new road charge from 2028 (3p per mile); plug-in hybrids pay 1.5p per mile.\n- The cash ISA limit for under-65s will fall and the state pension and many benefits will rise in April.",
              "summaryTo": "- 財政大臣公布預算，包含關於稅務、汽車與退休金的主要變動。\n- 稅級將凍結到2031年；薪資增加可能使部分人進入較高稅率。\n- 電動車自2028年起將付新路費（每英里3便士）；插電式混合車為1.5便士每英里。\n- 65歲以下的現金ISA上限將下降，國家退休金與多項福利在四月會增加。",
              "summary_en": "- The Chancellor announced the Budget with key changes on taxes, cars and pensions.\n- Tax bands are frozen until 2031; this may move some people into higher tax rates.\n- Electric cars will pay a new road charge from 2028 (3p per mile); plug-in hybrids pay 1.5p per mile.\n- The cash ISA limit for under-65s will fall and the state pension and many benefits will rise in April.",
              "summary_zh": "- 財政大臣公布預算，包含關於稅務、汽車與退休金的主要變動。\n- 稅級將凍結到2031年；薪資增加可能使部分人進入較高稅率。\n- 電動車自2028年起將付新路費（每英里3便士）；插電式混合車為1.5便士每英里。\n- 65歲以下的現金ISA上限將下降，國家退休金與多項福利在四月會增加。",
              "requested_difficulties": [
                "VERY_EASY"
              ],
              "from_language": "en",
              "to_language": "zh-TW",
              "content": {
                "difficulty": "VERY_EASY",
                "from_language": "en",
                "to_language": "zh-TW",
                "optimized_html_from": "<article>\n  <h1>Isas, cars and pensions: How the Budget affects you</h1>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp\" alt=\"Mother carrying her young daughter while unloading groceries into their white electric car in a supermarket\" />\n    <figcaption>Getty Images: A mother with her child and an electric car</figcaption>\n  </figure>\n\n  <p>Chancellor Rachel Reeves announced the Budget. The official forecaster published details early. This article shows the key changes and how they affect you.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp\" alt=\"Table showing income tax levels and personal allowance figures\" />\n    <figcaption>Table: Income tax bands and allowances</figcaption>\n  </figure>\n\n  <p>Tax bands will stay frozen until 2031. A pay rise can push some people into a higher tax rate. VAT and many other taxes stay the same.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp\" alt=\"A white Tesla electric car charging at a public charging station\" />\n    <figcaption>John Keeble/Getty Images: An electric car at a charging station</figcaption>\n  </figure>\n\n  <p>Electric cars will face a new road charge from 2028. Electric cars will pay 3p per mile and plug-in hybrids 1.5p per mile. The government will also keep some fuel duty changes for now and then change them later.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp\" alt=\"Table showing council tax bands in England based on property values\" />\n    <figcaption>Table: Council tax bands and values</figcaption>\n  </figure>\n\n  <p>Homes worth \u0000A32m or more will face a council tax surcharge from April 2028. The surcharge will have several price bands and affect many homes in expensive areas. The government will revalue the top council tax bands for the first time since 1991.</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp\" alt=\"Table showing sugar in milk-based drinks compared with other drinks\" />\n    <figcaption>Table: Sugar content in some drinks</figcaption>\n  </figure>\n\n  <p>The cash ISA limit for under 65s will fall from \u0000A320,000 to \u0000A312,000 each year. The government wants people to invest more in other accounts. The state pension will rise and many benefits will rise in April.</p>\n\n  <p>The two-child cap on some benefits will end next April. The minimum wage will go up in April and many workers will get a pay rise. Rules on pension salary sacrifice will change and there will be a cap from April 2029.</p>\n</article>",
                "optimized_html_to": "<article>\n  <h1>Isas、車子和養老金：預算如何影響你</h1>\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b10/live/22c26430-cae2-11f0-a892-01d657345866.jpg.webp\" alt=\"母親抱著小孩，在超市把雜貨放進白色電動車\" />\n    <figcaption>圖片：母親抱著小孩，和電動車</figcaption>\n  </figure>\n\n  <p>財政大臣 Rachel Reeves 公布了預算。官方預測機構提早發佈了細節。這裡有主要改變和它們對你的影響。</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6485/live/2bbb3e40-cadd-11f0-9fb5-5f3a3703a365.png.webp\" alt=\"顯示所得稅級距和免稅額的表格\" />\n    <figcaption>表格：所得稅級距和免稅額</figcaption>\n  </figure>\n\n  <p>稅率級距會凍結到2031年。加薪可能會讓你付更多稅。消費稅（VAT）保持不變。</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/d6eb/live/367b2510-caca-11f0-8c06-f5d460985095.jpg.webp\" alt=\"白色特斯拉在公共充電站充電\" />\n    <figcaption>圖片：電動車在充電站充電</figcaption>\n  </figure>\n\n  <p>電動車從2028年要付新的路費。電動車每英里付3便士，插電式混合車付1.5便士。最低工資在四月會上調，許多工人會加薪。</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/6b3a/live/cba29600-caca-11f0-8c06-f5d460985095.png.webp\" alt=\"顯示英格蘭市政稅級距與房價對照的表格\" />\n    <figcaption>表格：市政稅級距與房價</figcaption>\n  </figure>\n\n  <p>價值兩百萬英鎊或以上的房子，從2028年起要付市政稅附加費。附加費有不同級別，最高級別金額更高。政府會為最高的稅級做房價重新估值。</p>\n\n  <figure>\n    <img srcset=\"https://ichef.bbci.co.uk/news/240/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 240w, https://ichef.bbci.co.uk/news/320/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 320w, https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 480w, https://ichef.bbci.co.uk/news/640/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 640w, https://ichef.bbci.co.uk/news/800/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 800w, https://ichef.bbci.co.uk/news/1024/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1024w, https://ichef.bbci.co.uk/news/1536/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp 1536w\" src=\"https://ichef.bbci.co.uk/news/480/cpsprodpb/abe6/live/093b4fb0-cac7-11f0-a892-01d657345866.png.webp\" alt=\"顯示含糖量的飲品比較表\" />\n    <figcaption>表格：部分飲品的含糖量比較</figcaption>\n  </figure>\n\n  <p>65歲以下的現金ISA上限會從每年20,000英鎊降到12,000英鎊。政府希望人們多投資在其他帳戶。國家養老金和許多福利會在四月上調。</p>\n\n  <p>兩孩上限在明年四月會取消。最低工資在四月會增加。養老金薪資換取方案會有每年2,000英鎊的限制，從2029年起生效。</p>\n</article>",
                "cleaned_text_from": "Chancellor Rachel Reeves announced the Budget. The official forecaster published details early. This article shows the key changes and how they affect you.\n\nTax bands will stay frozen until 2031. A pay rise can push some people into a higher tax rate. VAT and many other taxes stay the same.\n\nElectric cars will face a new road charge from 2028. Electric cars will pay 3p per mile and plug-in hybrids 1.5p per mile. The government will also keep some fuel duty changes for now and then change them later.\n\nHomes worth \u0000A32m or more will face a council tax surcharge from April 2028. The surcharge will have several price bands and affect many homes in expensive areas. The government will revalue the top council tax bands for the first time since 1991.\n\nThe cash ISA limit for under 65s will fall from \u0000A320,000 to \u0000A312,000 each year. The government wants people to invest more in other accounts. The state pension will rise and many benefits will rise in April.\n\nThe two-child cap on some benefits will end next April. The minimum wage will go up in April and many workers will get a pay rise. Rules on pension salary sacrifice will change and there will be a cap from April 2029.",
                "cleaned_text_to": "財政大臣 Rachel Reeves 公布了預算。官方預測機構提早發佈了細節。這裡有主要改變和它們對你的影響。\n\n稅率級距會凍結到2031年。加薪可能會讓你付更多稅。消費稅（VAT）保持不變。\n\n電動車從2028年要付新的路費。電動車每英里付3便士，插電式混合車付1.5便士。最低工資在四月會上調，許多工人會加薪。\n\n價值兩百萬英鎊或以上的房子，從2028年起要付市政稅附加費。附加費有不同級別，最高級別金額更高。政府會為最高的稅級做房價重新估值。\n\n65歲以下的現金ISA上限會從每年20,000英鎊降到12,000英鎊。政府希望人們多投資在其他帳戶。國家養老金和許多福利會在四月上調。\n\n兩孩上限在明年四月會取消。最低工資在四月會增加。養老金薪資換取方案會有每年2,000英鎊的限制，從2029年起生效。",
                "explanation": {
                  "vocabulary": [
                    {
                      "pos": "noun",
                      "word_from": "budget",
                      "word_to": "預算",
                      "definition_from": "A plan for government money and spending.",
                      "definition_to": "政府關於收入與支出的計畫。",
                      "example_from": "The chancellor announced the budget today.",
                      "example_to": "財政大臣今天公布了預算。"
                    },
                    {
                      "pos": "noun",
                      "word_from": "tax",
                      "word_to": "稅",
                      "definition_from": "Money people pay to the government.",
                      "definition_to": "人民付給政府的錢。",
                      "example_from": "We pay tax when we earn money.",
                      "example_to": "我們賺錢時要付稅。"
                    },
                    {
                      "pos": "noun",
                      "word_from": "car",
                      "word_to": "車（汽車）",
                      "definition_from": "A vehicle people use to travel on roads.",
                      "definition_to": "人們在道路上使用的交通工具。",
                      "example_from": "My car is blue.",
                      "example_to": "我的車是藍色的。"
                    },
                    {
                      "pos": "noun",
                      "word_from": "pension",
                      "word_to": "退休金",
                      "definition_from": "Money people get when they stop working.",
                      "definition_to": "人們停止工作後得到的錢。",
                      "example_from": "He gets a state pension after retirement.",
                      "example_to": "他退休後領國家退休金。"
                    },
                    {
                      "pos": "noun",
                      "word_from": "home",
                      "word_to": "家 / 房屋",
                      "definition_from": "A place where a person lives.",
                      "definition_to": "人居住的地方。",
                      "example_from": "Their home is in a big city.",
                      "example_to": "他們的家在大城市。"
                    },
                    {
                      "pos": "noun/verb",
                      "word_from": "charge",
                      "word_to": "費用 / 收費",
                      "definition_from": "A fee you must pay; to ask for money.",
                      "definition_to": "你必須付的費用；收取錢。",
                      "example_from": "There is a new road charge for electric cars.",
                      "example_to": "電動車有新的路費。"
                    },
                    {
                      "pos": "noun/verb",
                      "word_from": "rise",
                      "word_to": "增加 / 上漲",
                      "definition_from": "An increase in number or amount.",
                      "definition_to": "數量或金額的增加。",
                      "example_from": "The state pension will rise in April.",
                      "example_to": "國家退休金在四月會增加。"
                    },
                    {
                      "pos": "noun",
                      "word_from": "limit",
                      "word_to": "上限 / 限制",
                      "definition_from": "The highest amount allowed.",
                      "definition_to": "允許的最高數量。",
                      "example_from": "The cash ISA limit will fall for under-65s.",
                      "example_to": "65歲以下的現金ISA上限會下降。"
                    }
                  ],
                  "grammar": [
                    {
                      "rule_from": "Present Simple (S V O)",
                      "rule_to": "現在簡單式（主詞 + 動詞 + 受詞）",
                      "explanation_from": "Use the present simple for routines, facts, and things that are always true. Structure: Subject + base verb (+ s/es for he/she/it).",
                      "explanation_to": "用現在簡單式說習慣、事實或普遍真理。結構：主詞 + 動詞原形（第三人稱單數加 s/es）。",
                      "example_from": "The government keeps many taxes the same.",
                      "example_to": "政府維持許多稅項不變。"
                    },
                    {
                      "rule_from": "Past Simple",
                      "rule_to": "過去簡單式",
                      "explanation_from": "Use the past simple for actions that finished in the past. Structure: Subject + past form of verb.",
                      "explanation_to": "用過去簡單式表示已經完成的過去動作。結構：主詞 + 動詞過去式。",
                      "example_from": "The official forecaster published details early.",
                      "example_to": "官方預測機構較早公布了細節。"
                    },
                    {
                      "rule_from": "SVO sentence structure",
                      "rule_to": "主動詞型句構（S V O）",
                      "explanation_from": "A simple English sentence usually has Subject + Verb + Object. This order is common and clear.",
                      "explanation_to": "簡單英文句子通常是主詞 + 動詞 + 受詞。這個語序常見且清楚。",
                      "example_from": "Chancellor (S) announced (V) the Budget (O).",
                      "example_to": "財政大臣（主詞）公布（動詞）預算（受詞）。"
                    }
                  ],
                  "sentence_patterns": [
                    {
                      "pattern_from": "There is / There are + noun",
                      "pattern_to": "There is / There are + 名詞",
                      "explanation_from": "Use 'There is' for one thing and 'There are' for more than one. It shows existence.",
                      "explanation_to": "'There is' 用於一個事物，'There are' 用於多個。表示存在。",
                      "example_from": "There is a new charge for electric cars.",
                      "example_to": "電動車有新的收費。"
                    },
                    {
                      "pattern_from": "I/We/They like + noun/verb-ing",
                      "pattern_to": "I/We/They like + 名詞 / 動名詞",
                      "explanation_from": "Use this to say you enjoy something.",
                      "explanation_to": "用來說你喜歡某事。",
                      "example_from": "I like to save money in my account.",
                      "example_to": "我喜歡把錢存進我的帳戶。"
                    },
                    {
                      "pattern_from": "Subject + verb + amount (e.g., pay X per mile)",
                      "pattern_to": "主詞 + 動詞 + 數量（例如：pay X per mile）",
                      "explanation_from": "Use this pattern for costs and measurements.",
                      "explanation_to": "用此句型表示費用或度量。",
                      "example_from": "Electric cars pay 3p per mile.",
                      "example_to": "電動車每英里付3便士。"
                    }
                  ],
                  "phrases_idioms": [
                    {
                      "phrase_from": "pay a charge",
                      "phrase_to": "支付費用",
                      "explanation_from": "To give money for a fee or cost.",
                      "explanation_to": "為了某項費用或成本付錢。",
                      "example_from": "Drivers must pay a new road charge from 2028.",
                      "example_to": "駕駛人自2028年起必須支付新的道路費用。"
                    },
                    {
                      "phrase_from": "face a surcharge",
                      "phrase_to": "面臨附加費",
                      "explanation_from": "To have to accept an extra tax or fee.",
                      "explanation_to": "必須接受額外的稅或費用。",
                      "example_from": "Homes worth a high amount will face a council tax surcharge.",
                      "example_to": "價值高的房屋將面臨市政稅的附加費。"
                    },
                    {
                      "phrase_from": "fall from ... to ...",
                      "phrase_to": "從…降到…",
                      "explanation_from": "To show that a number goes down from one value to another.",
                      "explanation_to": "表示數字從一個值降到另一個值。",
                      "example_from": "The ISA limit will fall from £20,000 to £12,000.",
                      "example_to": "ISA上限將從£20,000降到£12,000。"
                    },
                    {
                      "phrase_from": "keep ... for now",
                      "phrase_to": "暫時保留…",
                      "explanation_from": "To continue something for the present time.",
                      "explanation_to": "在目前這段時間內繼續保留某事。",
                      "example_from": "The government will keep some fuel duty changes for now.",
                      "example_to": "政府暫時保留一些燃料稅的變動。"
                    }
                  ],
                  "comprehension_mcq": [
                    {
                      "question_from": "When will the new road charge for electric cars start?",
                      "question_to": "電動車的新路費何時開始？",
                      "options": [
                        {
                          "label": "A",
                          "text_from": "2025",
                          "text_to": "2025年"
                        },
                        {
                          "label": "B",
                          "text_from": "2028",
                          "text_to": "2028年"
                        },
                        {
                          "label": "C",
                          "text_from": "2031",
                          "text_to": "2031年"
                        },
                        {
                          "label": "D",
                          "text_from": "2023",
                          "text_to": "2023年"
                        }
                      ],
                      "answer": "B",
                      "explanation_from": "The article says electric cars will face a new road charge from 2028.",
                      "explanation_to": "文章說電動車自2028年起會有新的路費。"
                    },
                    {
                      "question_from": "How much will electric cars pay per mile?",
                      "question_to": "電動車每英里要付多少錢？",
                      "options": [
                        {
                          "label": "A",
                          "text_from": "1p per mile",
                          "text_to": "每英里1便士"
                        },
                        {
                          "label": "B",
                          "text_from": "3p per mile",
                          "text_to": "每英里3便士"
                        },
                        {
                          "label": "C",
                          "text_from": "10p per mile",
                          "text_to": "每英里10便士"
                        },
                        {
                          "label": "D",
                          "text_from": "No charge",
                          "text_to": "不用付費"
                        }
                      ],
                      "answer": "B",
                      "explanation_from": "The text states electric cars will pay 3p per mile.",
                      "explanation_to": "文本指出電動車每英里付3便士。"
                    },
                    {
                      "question_from": "What happens to the cash ISA limit for under-65s?",
                      "question_to": "65歲以下的現金ISA上限會怎樣？",
                      "options": [
                        {
                          "label": "A",
                          "text_from": "It will increase",
                          "text_to": "上限會增加"
                        },
                        {
                          "label": "B",
                          "text_from": "No change",
                          "text_to": "沒有改變"
                        },
                        {
                          "label": "C",
                          "text_from": "It will fall",
                          "text_to": "上限會下降"
                        },
                        {
                          "label": "D",
                          "text_from": "It will be removed",
                          "text_to": "上限會被取消"
                        }
                      ],
                      "answer": "C",
                      "explanation_from": "The article says the cash ISA limit for under 65s will fall (be reduced).",
                      "explanation_to": "文章說65歲以下的現金ISA上限會下降（減少）。"
                    },
                    {
                      "question_from": "What will the government do to top council tax bands?",
                      "question_to": "政府會對最高的市政稅級做什麼？",
                      "options": [
                        {
                          "label": "A",
                          "text_from": "Remove them",
                          "text_to": "移除這些稅級"
                        },
                        {
                          "label": "B",
                          "text_from": "Revalue them for the first time since 1991",
                          "text_to": "自1991年以來首次重新估價"
                        },
                        {
                          "label": "C",
                          "text_from": "Make them lower",
                          "text_to": "把它們調低"
                        },
                        {
                          "label": "D",
                          "text_from": "Ignore them",
                          "text_to": "忽視它們"
                        }
                      ],
                      "answer": "B",
                      "explanation_from": "The article says the government will revalue the top council tax bands for the first time since 1991.",
                      "explanation_to": "文章說政府將自1991年以來首次重新估價最高的市政稅級。"
                    }
                  ]
                }
              },
              "available_difficulties": [
                "VERY_EASY"
              ],
              "available_views": [
                {
                  "from_language": "en",
                  "to_language": "zh-TW",
                  "difficulty": "VERY_EASY"
                }
              ]
            }
          ]
        }
    """.trimIndent()
    val mockResponse: String = """
        {
          "message": "success",
          "requestedCount": 2,
          "actualCount": 2,
          "totalAvailable": 2,
          "filters": {
            "source": "BBC",
            "section": "technology",
            "difficulty": "NORMAL",
            "from_language": "en",
            "to_language": "zh-TW"
          },
          "articles": [
            {
              "id": 1,
              "sourceName": "BBC",
              "section": "technology",
              "title": "Original title",
              "publishedAt": "2024-01-01T00:00:00Z",
              "originalUrl": "https://example.com/1",
              "contentHtml": "<p>Original content</p>",
              "titleFrom": "Original title",
              "titleTo": "原始標題",
              "title_zh": "原始標題(legacy)",
              "summaryFrom": "Summary EN",
              "summaryTo": "摘要中文",
              "summary_en": "Legacy summary en",
              "summary_zh": "Legacy summary zh",
              "requested_difficulty": "NORMAL",
              "from_language": "en",
              "to_language": "zh-TW",
              "content": {
                "difficulty": "NORMAL",
                "from_language": "en",
                "to_language": "zh-TW",
                "optimized_html_from": "<p>Optimized EN</p>",
                "optimized_html_to": "<p>優化中文</p>",
                "cleaned_text_from": "Optimized EN",
                "cleaned_text_to": "優化中文",
                "explanation": {
                  "vocabulary": [
                    {
                      "pos": "n.",
                      "word_from": "planet",
                      "word_to": "行星",
                      "definition_from": "A celestial body",
                      "definition_to": "天體",
                      "example_from": "Earth is a planet.",
                      "example_to": "地球是一顆行星。"
                    }
                  ],
                  "grammar": [
                    {
                      "rule_from": "Past tense",
                      "rule_to": "過去式",
                      "explanation_from": "Used for past actions",
                      "explanation_to": "用於表示過去的動作",
                      "example_from": "I walked.",
                      "example_to": "我走了。"
                    }
                  ],
                  "sentence_patterns": [
                    {
                      "pattern_from": "S + V + O",
                      "pattern_to": "主詞 + 動詞 + 受詞",
                      "explanation_from": "Basic sentence",
                      "explanation_to": "基本句型",
                      "example_from": "She eats apples.",
                      "example_to": "她吃蘋果。"
                    }
                  ],
                  "phrases_idioms": [
                    {
                      "phrase_from": "break a leg",
                      "phrase_to": "祝好運",
                      "explanation_from": "Good luck",
                      "explanation_to": "祝你成功",
                      "example_from": "Break a leg in your exam!",
                      "example_to": "考試加油！"
                    }
                  ],
                  "comprehension_mcq": [
                    {
                      "question_from": "What is Earth?",
                      "question_to": "地球是什麼？",
                      "options": [
                        {"label": "A", "text_from": "A star", "text_to": "恆星"},
                        {"label": "B", "text_from": "A planet", "text_to": "行星"}
                      ],
                      "answer": "B",
                      "explanation_from": "Earth is a planet.",
                      "explanation_to": "地球是一顆行星。"
                    }
                  ]
                }
              },
              "available_difficulties": ["EASY", "NORMAL", "HARD"],
              "available_views": [
                {"from_language": "en", "to_language": "zh-TW", "difficulty": "NORMAL"}
              ]
            },
            {
              "id": 2,
              "sourceName": "BBC",
              "section": "technology",
              "title": "Second title",
              "publishedAt": "2024-01-02T00:00:00Z",
              "originalUrl": "https://example.com/2",
              "contentHtml": "<p>Another article</p>",
              "titleFrom": "Second title",
              "titleTo": "第二標題",
              "summaryFrom": "Second summary",
              "summaryTo": "第二摘要",
              "from_language": "en",
              "to_language": "zh-TW",
              "content": {
                "difficulty": "NORMAL",
                "from_language": "en",
                "to_language": "zh-TW",
                "optimized_html_from": "<p>Optimized second EN</p>",
                "optimized_html_to": "<p>優化第二中文</p>",
                "cleaned_text_from": "Optimized second EN",
                "cleaned_text_to": "優化第二中文"
              },
              "available_difficulties": ["NORMAL"],
              "available_views": [
                {"from_language": "en", "to_language": "zh-TW", "difficulty": "NORMAL"}
              ]
            }
          ]
        }
    """.trimIndent()
}