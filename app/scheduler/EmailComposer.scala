package scheduler

import model.AppInstance
import repo.AppStatusRepository.Status

/**
  * Created by chlr on 7/8/17.
  */
class EmailComposer(appInstance: AppInstance, jobName: String, groupName: String) {


  private def getStatus(id: Int) = {
    id match {
      case Status.fail => "failed"
      case Status.error => "error"
      case Status.success => "succeeded"
      case Status.finished => "force finished"
    }
  }

  def subject = s"Valkyrie Alert: Job name: $jobName; Group name: $groupName"

  private val message = s"Your job $jobName in group $groupName has ${getStatus(appInstance.statusId)}"

  private val tableHtml = {
    val fields = ("Instance-Id" -> appInstance.id)  :: ("Start-Time" -> appInstance.startTime) ::
      ("End-Time" -> appInstance.endTime.getOrElse("")) :: ("Status" -> getStatus(appInstance.statusId)) ::
      ("Agent" -> appInstance.agent) :: ("Message" -> appInstance.message.getOrElse("")) :: Nil
    for {
      (key, value) <- fields
    }  yield {
      s"""
        |<tr style="text-align:center;background-color:white">
        |    <td style="color:black;white-space:nowrap;font-size:12px;text-align:left;border-width:1px;padding:8px;border-style:double;border-color:#a9a9a9">$key</td>
        |    <td style="color:black;white-space:nowrap;font-size:12px;text-align:left;border-width:1px;padding:8px;border-style:double;border-color:#a9a9a9">$value</td>
        |</tr>
      """.stripMargin
    }
  }

  def template =
    s"""
       |<div link="#355491" alink="#4262a1" vlink="#355491" style="background:#e2e2e2;margin:0;padding:20px">
       |    <div>
       |        <table cellpadding="0" bgcolor="#FFFFFF" border="0" cellspacing="0" style="border:1px solid #dadada;margin-bottom:30px;width:100%">
       |            <tbody>
       |            <tr>
       |                <td bgcolor="#000000" valign="middle" height="58px" style="border-bottom:1px solid #ccc;padding:20px">
       |                    <h1 style="color:#FFFFFF;font:bold 22px Arial,Helvetica,sans-serif;margin:0;display:block!important">Valkyrie Scheduler</h1>
       |                </td>
       |            </tr>
       |            <tr>
       |                <td bgcolor="#FFFFFF" style="font:normal 12px Arial,Helvetica,sans-serif;color:#333333;padding:20px">
       |                    <p></p>
       |                    <p>Hi $groupName,<br><br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; $message
       |                        <br>
       |                        <br><br></p>
       |                    <table style="font-size:12px;color:#333333;width:30%;border-width:1px;border-color:#a9a9a9;border-collapse:collapse;margin-bottom:10px" border="1" align="left">
       |                        <tbody>
       |                        ${tableHtml.mkString("\n")}
       |                        </tbody>
       |                    </table>
       |                </td>
       |            </tr>
       |            <tr>
       |                <td bgcolor="#FFFFFF" style="font:normal 12px Arial,Helvetica,sans-serif;color:#333333;padding:20px">
       |                    <p>Yours truly,<br>Valkyrie Scheduler</p>
       |                </td>
       |            </tr>
       |            </tbody>
       |        </table>
       |        <div class="yj6qo"></div>
       |        <div class="adL"></div>
       |    </div>
       |    <div class="adL"></div>
       |</div>
    """.stripMargin
}
