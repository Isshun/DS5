game.data:extend(
{
    {
        type = "list",
        name = "ui-debug",
        position = {100, 100},
        size = {200, 500},
        background = 0x552200,
        visible = true,
        views=
        {
            {type="label", text="Add item...",          text_size=14,   padding=5,  on_click="game.crew:add()"},
            {type="label", text="Add structure...",     text_size=14,   padding=5,  on_click=""},
            {type="label", text="Add consumable...",    text_size=14,   padding=5,  on_click=""},
            {type="label", text="Add resource...",      text_size=14,   padding=5,  on_click=""},
            {type="label", text="Regen world",          text_size=14,   padding=5,  on_click=""},
            {type="label", text="Add crew (human)",     text_size=14,   padding=5,  on_click="game.crew:add()"},
            {type="label", text="Add crew (droid)",     text_size=14,   padding=5,  on_click="game.crew:add()"},
            {type="label", text="Add crew (android)",   text_size=14,   padding=5,  on_click="game.crew:add()"},
            {type="label", text="Kill selected",        text_size=14,   padding=5,  on_click="game.crew:remove(game.crew:getSelected())"},
            {type="label", text="Kill all",             text_size=14,   padding=5,  on_click=""},
            {type="label", text="Launch quest",         text_size=14,   padding=5,  on_click=""},
            {type="label", text="Refresh rooms",        text_size=14,   padding=5,  on_click=""},
            {type="label", text="Remove rubbles",       text_size=14,   padding=5,  on_click=""},
            {type="label", text="Set need...",          text_size=14,   padding=5,  on_click=""},
            {type="label", text="Set energy 0",         text_size=14,   padding=5,  on_click="game.crew:getSelected():getNeeds().energy = 0"},
            {type="label", text="Dump modules",         text_size=14,   padding=5,  on_click=""},
            {type="label", text="Dump renders",         text_size=14,   padding=5,  on_click=""},
            {type="label", text="Temperature debug",    text_size=14,   padding=5,  on_click=""},
            {type="label", text="Oxygen debug",         text_size=14,   padding=5,  on_click=""},
            {type="label", text="Job debug",            text_size=14,   padding=5,  on_click=""},
            {type="label", text="Parcel debug",         text_size=14,   padding=5,  on_click=""},
        },
    },
}
)