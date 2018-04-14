package com.claassen.reactPlayground.components.tutorial

import com.claassen.reactPlayground.components.tutorial.ReactCounterWithDiode.AppCircuit
import diode.react.{ModelProxy, ReactConnector}
import diode.{Action, ActionHandler, Circuit}
import japgolly.scalajs.react.{BackendScope, _}
import japgolly.scalajs.react.vdom.html_<^._

object ReactCounterWithDiode {

  // Define the root of our application model
  case class RootModel(counter: Int)

  // Define actions
  case object Increase extends Action

  case object Decrease extends Action

  case object Reset extends Action

  object ReactCounter {

    class Backend($: BackendScope[ModelProxy[RootModel], Unit]) {
      def render(props: ModelProxy[RootModel]) = {
        <.div(
          <.h3("Counter"),
          <.p("Value = ", <.b(props().counter)),
          <.div(
            ^.className := "btn-group",
            <.button(
              ^.className := "btn btn-default",
              ^.onClick --> props.dispatchCB(Increase),
              "Increase"
            ),
            <.button(
              ^.className := "btn btn-default",
              ^.onClick --> props.dispatchCB(Decrease),
              "Decrease"
            ),
            <.button(
              ^.className := "btn btn-default",
              ^.onClick --> props.dispatchCB(Reset),
              "Reset"
            )
          )
        )
      }
    }

    val component = ScalaComponent.builder[ModelProxy[RootModel]]("Counter")
      .renderBackend[Backend]
      .build

    def apply(props: ModelProxy[RootModel]) =
      component(props)
  }

  object AppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
    // define initial value for the application model
    def initialModel = RootModel(0)

    val counterHandler = new ActionHandler(zoomTo(_.counter)) {
      override def handle = {
        case Increase => updated(value + 1)
        case Decrease => updated(value - 1)
        case Reset => updated(0)
      }
    }

    override protected def actionHandler: AppCircuit.HandlerFunction = counterHandler
  }

  val connection = AppCircuit.connect(x => x)

  def apply() = connection { p =>
    ReactCounter(p)
  }.vdomElement
}