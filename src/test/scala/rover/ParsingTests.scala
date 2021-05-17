package rover

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SitterReview.{parseReview, parseLines}

class ParsingTests extends AnyFlatSpec with Matchers {

  "parseReview" should "extract correct fields" in {
    val reviewLine = List("5", "https://images.dog.ceo/breeds/affenpinscher/n02110627_10437.jpg", "2013-04-17", "Augue donec...ok cool", "https://images.dog.ceo/breeds/puggle/IMG_095543.jpg", "Bandit|Duke|Pogo", "Joei B.", "Melinda G.", "2013-04-13", "+17953174861", "user1943@verizon.net", "+13494618669", "user4832@t-mobile.com", "110")
    parseReview(reviewLine).toOption.get shouldEqual SitterReview("Joei B.", 5, "user1943@verizon.net")
  }

  "parseLines" should "ignore csv header and construct Reviews with correct info" in {
    val lines = Iterator(
      "rating,sitter_image,end_date,text,owner_image,dogs,sitter,owner,start_date,sitter_phone_number,sitter_email,owner_phone_number,owner_email,response_time_minutes",
      "5,https://images.dog.ceo/breeds/dalmatian/cooper2.jpg,2013-04-08,Donec lacus justo luctus tellus nisl penatibus mus massa ipsum odio. Lorem dolor. Fames lorem ligula fusce condimentum dis mauris. Metus nulla quam mus duis congue volutpat et ipsum ad. Purus netus a viverra et sapien et pharetra quis nullam posuere amet sem convallis etiam sagittis vel. Nulla donec suspendisse sagittis hymenaeos mi. Metus risus enim egestas. Fames vitae mus vivamus eu ad donec cum elit consectetuer. Purus magna per rutrum fusce condimentum habitant quis pretium ac egestas diam sapien leo tortor rutrum. Felis vitae fames velit. Ipsum morbi interdum. Neque justo gravida cras at.,https://images.dog.ceo/breeds/hound-ibizan/n02091244_327.jpg,Pinot Grigio,Lauren B.,Shelli K.,2013-02-26,+12546478758,user4739@gmail.com,+15817557107,user2555@verizon.net,2",
      "3,https://images.dog.ceo/breeds/entlebucher/n02108000_2635.jpg,2012-05-17,Etiam morbi duis sed. Etiam netus torquent donec. Justo felis risus. Neque risus enim tortor curabitur ut dolor per. Felis vitae mus quam eget sagittis lacus convallis vestibulum. Netus vitae. Curae proin aenean augue curae aptent dui potenti. Fusce magna. Donec etiam. Fusce dolor quis urna turpis vulputate. Purus donec ornare suspendisse proin facilisis urna sem. Metus risus nisi sagittis ullamcorper sem. Morbi class lobortis. Curae ipsum vulputate sodales pellentesque dapibus. Donec vitae dictum sollicitudin potenti metus nunc dolor ad. Nulla donec. Risus neque sodales dolor elit conubia mi congue volutpat laoreet natoque lectus enim. Vitae metus eni. Morbi etiam tristique condimentum est. Neque massa.,https://images.dog.ceo/breeds/shihtzu/n02086240_1215.jpg,Rasty-CAT|Shogun|Annie,Leilani R.,Nancy L.,2012-04-03,+13813114382,user7508@t-mobile.com,+11826582052,user3444@t-mobile.com,186"
    )

    parseLines(lines).toOption.get should contain theSameElementsAs List(
      SitterReview("Lauren B.", 5, "user4739@gmail.com"),
      SitterReview("Leilani R.", 3, "user7508@t-mobile.com")
    )
  }
}