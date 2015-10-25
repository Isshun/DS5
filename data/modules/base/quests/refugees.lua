g_visitor = nil

data:extend({
    label = "Refugee",
    name = "base.quest_refugee",
    type = "quest",

    on_check = function (quest)
        quest.openMessage = "Un groupe de chercheurs s'est perdu lors d'une expédition et vous demande de\nles heberger pour la nuit, l'un d'eux semble être bléssé.\n\nIls n'ont helas aucuns biens de valeur à vous proposer en échange"
        quest.openOptions = {
            "Les loger et leur fournir du matériel / vivre",
            "Les loger uniquement",
            "Leur refuser l'entrée"
        }
        return true
    end,

    on_start = function (quest)
        if quest.option == 3 then
            return false
        end

        g_visitor = game.friendly:add(game.factory:createCharacter("human"))
        print("add " .. g_visitor.name)
    end,

    on_update = function (quest)
        return quest.tick > 100
    end,

    on_close = function (quest)
        if g_visitor and g_visitor:isAlive() then
            game.friendly:remove(g_visitor)

            local reward = math.random(3)
            if reward == 1 then
                quest.closeMessage = "Le groupe de chercheur est reparti sans encombre, pour vous remercier il vous\nlaisse leur robot de protocole: B5"
                quest.rewards:addCrew(game.factory:createCharacter("droid"))
            elseif reward == 2 then
                quest.closeMessage = "Le groupe de chercheur est parvenu à rentrer à leur base et il vous envoie en\nremerciment des fournitures medicales"
                quest.rewards:addConsumable(game.factory:createConsumable("base.seaweed", 20))
            else
                quest.closeMessage = "Le groupe de chercheur est parvenu à rentrer à leur base, ils n'ont aucun biens\nà vous envoyer mais vous fournissent une copie de leur recherches"
                quest.rewards:addResource("science", 100)
            end
            return true
        end

        return false
    end,
})