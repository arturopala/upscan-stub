package services

import model.initiate.{PrepareUploadResponse, UploadSettings}
import org.scalatest.Matchers
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.test.UnitSpec
import utils.Implicits._

class PrepareUploadServiceSpec extends UnitSpec with Matchers {
  "PrepareUploadService.prepareUpload" should {
    val testInstance = new PrepareUploadService()
    val userAgent    = Some("PrepareUploadServiceSpec")

    val uploadSettings = UploadSettings(
      uploadUrl           = "uploadUrl",
      callbackUrl         = "callbackUrl",
      minimumFileSize     = None,
      maximumFileSize     = None,
      expectedContentType = None,
      successRedirect     = None,
      errorRedirect       = None
    )

    "include supplied file size constraints in the policy" in {
      val result: PrepareUploadResponse =
        testInstance
          .prepareUpload(uploadSettings.copy(minimumFileSize = Some(10), maximumFileSize = Some(100)), userAgent)

      withMinMaxFileSizesInPolicyConditions(result) { (min, max) =>
        min shouldBe Some(10)
        max shouldBe Some(100)
      }
    }

    "include default file size constraints in the policy" in {
      val result: PrepareUploadResponse =
        testInstance.prepareUpload(uploadSettings.copy(minimumFileSize = None, maximumFileSize = None), userAgent)

      withMinMaxFileSizesInPolicyConditions(result) { (min, max) =>
        min shouldBe Some(0)
        max shouldBe Some(104857600)
      }
    }

    "include all required fields" in {
      val result: PrepareUploadResponse =
        testInstance.prepareUpload(uploadSettings, userAgent)

      result.uploadRequest.fields.keySet should contain theSameElementsAs Set(
        "acl",
        "key",
        "policy",
        "x-amz-algorithm",
        "x-amz-credential",
        "x-amz-date",
        "x-amz-meta-callback-url",
        "x-amz-meta-consuming-service",
        "x-amz-signature"
      )
    }
  }

  private def withMinMaxFileSizesInPolicyConditions[T](preparedUpload: PrepareUploadResponse)(
    block: (Option[Long], Option[Long]) => T): T = {

    val policy: String = preparedUpload.uploadRequest.fields("policy").base64decode()

    val policyJson: JsValue = Json.parse(policy)

    val conditions = (policyJson \ "conditions")(0)

    conditions(0).asOpt[String] shouldBe Some("content-length-range")

    block(conditions(1).asOpt[Long], conditions(2).asOpt[Long])
  }
}
