package controllers

import javax.inject._

import play.api.mvc._
import java.lang.ProcessBuilder.Redirect
import  play.api.libs.json._

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import models._

case class Item(id: Int, name: String, unit: String, amount: String, image: String, category: String)
case class ItemAndCost(item: Item, cost: Int)
case class StoreCalculation(storeId: Int, storeName: String, totalCost: Int, cart: List[ItemAndCost])
case class ItemSearchRequest(searchTerm: String)
case class CalculateCartRequest(zipCode: Int, itemIds: List[Int])
case class Store(storeId: Int, storeName: String, zipCode: Int)

@Singleton
class ApiController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(implicit ec: ExecutionContext) 
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  implicit val itemReads: play.api.libs.json.Reads[controllers.Item] = Json.reads[Item]
  implicit val itemWrites: play.api.libs.json.OWrites[controllers.Item]= Json.writes[Item]
  implicit val itemAndCostReads: play.api.libs.json.Reads[controllers.ItemAndCost]= Json.reads[ItemAndCost]
  implicit val itemAndCostWrites: play.api.libs.json.OWrites[controllers.ItemAndCost]= Json.writes[ItemAndCost]
  implicit val storeCalculationReads: play.api.libs.json.Reads[controllers.StoreCalculation]= Json.reads[StoreCalculation]
  implicit val storeCalculationWrites: play.api.libs.json.OWrites[controllers.StoreCalculation]= Json.writes[StoreCalculation]
  implicit val itemSearchRequestReads: play.api.libs.json.Reads[controllers.ItemSearchRequest]= Json.reads[ItemSearchRequest]
  implicit val calculateCartRequestReads: play.api.libs.json.Reads[controllers.CalculateCartRequest]= Json.reads[CalculateCartRequest]
  private val model = new GroceryModel(db)

  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body => 
      Json.fromJson[A](body) match {
        case JsSuccess(a, path) => f(a)
        case e @ JsError(_) => 
          println(e)
          Future.successful(BadRequest(e.toString()))
      }  
    }.getOrElse(Future.successful(BadRequest("Bad request")))
  }
  
  def itemSearch(searchTerm: String) = Action.async { implicit request =>
    model.itemSearchByName(searchTerm).map { items =>
      Ok(Json.toJson(items))
    }
  }

  def calculateCart = Action.async { implicit request =>
    withJsonBody[CalculateCartRequest] { req =>
      val fStores = model.storeSearchByZip(req.zipCode)
      fStores.flatMap { stores =>
        model.calculateCart(stores, req.itemIds).map { res =>
          Ok(Json.toJson(res))
        }
      }
    }
  }
}

object ExampleObjects {
  val exampleItems = List(
    Item(323212, "Banana", "lb", "5", "https://picsum.photos/200/300", "produce"),
    Item(323213, "Apple", "lb", "5", "https://picsum.photos/200/300", "produce"),
    Item(323214, "Orange", "lb", "5", "https://picsum.photos/200/300", "produce"),
    Item(323215, "Grape", "lb", "5", "https://picsum.photos/200/300", "produce"),
    Item(323216, "Avocado", "lb", "5", "https://picsum.photos/200/300", "produce"),
    Item(323217, "Milk", "lb", "5", "https://picsum.photos/200/300", "produce"),
    Item(323218, "Pizza", "lb", "5", "https://picsum.photos/200/300", "produce"),
  )

  val exampleCartCalculation = List(
    StoreCalculation(141234, "HEB", 7000, 
      exampleItems.map(item => ItemAndCost(item, 1000))
    ),
    StoreCalculation(141235, "Sprouts", 14000, 
      exampleItems.map(item => ItemAndCost(item, 2000))
    ),
  )
}
