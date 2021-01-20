ui:extend({
    visible = false,
    type = "view",
    id = "base.ui.info_character.page_health",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoHealthController",
    views = {

        { type = "view", size = {200, 400}, position = {84, 70}, views = {
            { type = "image", id = "img_heart", src = "[base]/graphics/schema/schema_heart.png"},
            { type = "image", id = "img_body", src = "[base]/graphics/schema/schema_body.png"},
            { type = "image", id = "img_left_arm", src = "[base]/graphics/schema/schema_left_arm.png"},
            { type = "image", id = "img_left_eye", src = "[base]/graphics/schema/schema_left_eye.png"},
            { type = "image", id = "img_left_hand", src = "[base]/graphics/schema/schema_left_hand.png"},
            { type = "image", id = "img_left_head", src = "[base]/graphics/schema/schema_left_head.png"},
            { type = "image", id = "img_left_lung", src = "[base]/graphics/schema/schema_left_lung.png"},
            { type = "image", id = "img_left_shoulder", src = "[base]/graphics/schema/schema_left_shoulder.png"},
            { type = "image", id = "img_right_arm", src = "[base]/graphics/schema/schema_right_arm.png"},
            { type = "image", id = "img_right_eye", src = "[base]/graphics/schema/schema_right_eye.png"},
            { type = "image", id = "img_right_hand", src = "[base]/graphics/schema/schema_right_hand.png"},
            { type = "image", id = "img_right_head", src = "[base]/graphics/schema/schema_right_head.png"},
            { type = "image", id = "img_right_lung", src = "[base]/graphics/schema/schema_right_lung.png"},
            { type = "image", id = "img_right_shoulder", src = "[base]/graphics/schema/schema_right_shoulder.png"},
            { type = "image", src = "[base]/graphics/schema/schema.png"},
        }},

    }

})
